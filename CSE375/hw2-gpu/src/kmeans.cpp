// Implementation of the KMeans Algorithm
// reference: https://github.com/marcoscastro/kmeans

#include <iostream>
#include <vector>
#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <algorithm>
#include <chrono>
#include <atomic>
#include <mutex>
#include "tbb/tbb.h"

using namespace std;

class Point {
private:
	int id_point, id_cluster, total_values, K;
	vector<double> values, distance_to_clusters;
	string name;

public:
	Point(int id_point, vector<double>& values, int K, string name = "") : 
		id_point(id_point), 
		values(values), 
		total_values(values.size()), 
		name(name), 
		id_cluster(-1),
		K(K),
		distance_to_clusters(K, INFINITY) 
	{}

	int getID() {
		return id_point;
	}

	void setCluster(int id_cluster) {
		this->id_cluster = id_cluster;
	}

	int getCluster() {
		return id_cluster;
	}

	double getValue(int index) {
		return values[index];
	}

	int getTotalValues() {
		return total_values;
	}

	void addValue(double value) {
		values.push_back(value);
	}

	void setDistanceToCluster(int id_cluster, double distance) {
		distance_to_clusters[id_cluster] = distance;
	}


	double getDistanceToCluster(int id_cluster) {
		return distance_to_clusters[id_cluster];
	}

	string getName() {
		return name;
	}
};

class Cluster {
private:
	int id_cluster, total_points, total_values, last_change;
	vector<double> central_values, sums;
    mutex* point_lock;
public:
	Cluster(int id_cluster, Point point) : 
		id_cluster(id_cluster), 
		total_values(point.getTotalValues()),
		total_points(1), 
		last_change(0),
        point_lock(new mutex()) {
 
		for (int i = 0; i < total_values; i++) {
			double val = point.getValue(i);
			central_values.push_back(val);
			sums.push_back(val);
		}
	}

	void addPoint(Point point, int iteration) {
        lock_guard<mutex> lock(*point_lock);
		for (int i = 0; i < total_values; i++) {
			sums[i] += point.getValue(i);
		}
		total_points++;
		last_change = iteration;
	}

	void removePoint(Point point, int iteration) {
        lock_guard<mutex> lock(*point_lock);
		for (int i = 0; i < total_values; i++) {
			sums[i] -= point.getValue(i);
		}
		total_points--;
		last_change = iteration;
	}

	double getCentralValue(int index) {
		return central_values[index];
	}

	void setCentralValue(int index, double value) {
		central_values[index] = value;
	}

	int getTotalPoints() {
		return total_points;
	}

	int getID() {
		return id_cluster;
	}

	int getLastChange() {
		return last_change;
	}

	void recalculateCenter() {
		if (total_points == 0) return;
		const double tp = (double) total_points;
		for (int i = 0; i < total_values; i++) {
			central_values[i] = sums[i] / tp;
		}
	}
};

class KMeans {
private:
	int 
		K, // number of clusters
		total_values, // dimensions 
		total_points, // total points in the data
		max_iterations; // maximum iterations allowed before quitting
	vector<Cluster> clusters;

public:
	KMeans(int K, int total_points, int total_values, int max_iterations) {
		this->K = K;
		this->total_points = total_points;
		this->total_values = total_values;
		this->max_iterations = max_iterations;
	}

	void run(vector<Point> & points) {
        auto begin = chrono::high_resolution_clock::now();

		if (K > total_points)
			return;

		vector<int> prohibited_indexes;

		// choose K distinct values for the centers of the clusters
		for (int i = 0; i < K; i++) {
			while (true) {
				int index_point = rand() % total_points;

				if (find(prohibited_indexes.begin(), prohibited_indexes.end(), index_point) == prohibited_indexes.end()) {
					prohibited_indexes.push_back(index_point);
					points[index_point].setCluster(i);
					Cluster cluster(i, points[index_point]);
					clusters.push_back(cluster);
					break;
				}
			}
		}
        auto end_phase1 = chrono::high_resolution_clock::now();

		int iter = 1;
		while (true) {
			atomic<bool> done = {true};

			// associates each point to the nearest center
			tbb::parallel_for(
				tbb::blocked_range<int>(0, total_points),
					[&](tbb::blocked_range<int> r) {
						for (size_t i = r.begin(); i != r.end(); ++i) {
							Point &p = points[i];
							double min_dist = INFINITY;
							int id_old_cluster = p.getCluster(), id_nearest_center = 0;
							
							for (int cluster_id = 0; cluster_id < K; cluster_id++) { // loop through all the clusters (find closest cluster)
								Cluster &c = clusters[cluster_id];
								if (c.getLastChange() >= iter - 1) { // check the last time it was updated (changed since last iteration)
									double sum = 0.0;
									
									for (int j = 0; j < total_values; j++) {
										double diff = c.getCentralValue(j) - p.getValue(j);
										sum += diff * diff;
									}

									p.setDistanceToCluster(cluster_id, sum); // set the distance to the current cluster (k) for the point
								}
								
								double dist = p.getDistanceToCluster(cluster_id);
								if (dist < min_dist) {
									min_dist = dist;
									id_nearest_center = cluster_id;
								}
							}

							if (id_old_cluster != id_nearest_center) { // cluster has changed
								if (id_old_cluster != -1)
									clusters[id_old_cluster].removePoint(p, iter);

								p.setCluster(id_nearest_center);
								clusters[id_nearest_center].addPoint(p, iter);
								done = false;
							}
						}
					}
				);

			// Move this if statment to ensure that I don't have to recalcualte center if there is nothing to be done
			if (done == true || iter >= max_iterations) {
				// cout << "Break in iteration " << iter << "\n\n";
				break;
			}

			// recalculating the center of each cluster
			tbb::parallel_for(
				tbb::blocked_range<int>(0, K),
					[&](tbb::blocked_range<int> r) {
						for (size_t j = r.begin(); j != r.end(); ++j) {
							clusters[j].recalculateCenter();
						}
					}
				);


			iter++;
		}
        auto end = chrono::high_resolution_clock::now();

		// shows elements of clusters
		// for (int i = 0; i < K; i++) {
		// 	int total_points_cluster =  clusters[i].getTotalPoints();

		// 	cout << "Cluster " << clusters[i].getID() + 1 << endl;
		// 	// for (int j = 0; j < total_points_cluster; j++) {
		// 	// 	cout << "Point " << clusters[i].getPoint(j).getID() + 1 << ": ";
		// 	// 	for (int p = 0; p < total_values; p++)
		// 	// 		cout << clusters[i].getPoint(j).getValue(p) << " ";

		// 	// 	string point_name = clusters[i].getPoint(j).getName();

		// 	// 	if (point_name != "")
		// 	// 		cout << "- " << point_name;

		// 	// 	cout << endl;
		// 	// }

		// 	cout << "Cluster values: ";

		// 	for (int j = 0; j < total_values; j++)
		// 		cout << clusters[i].getCentralValue(j) << " ";

		// 	cout << "\n\n";
            
		// }
		// cout << "TOTAL EXECUTION TIME = " << std::chrono::duration_cast<std::chrono::microseconds>(end - begin).count() << "\n";

		// cout << "TIME PHASE 1 = " << std::chrono::duration_cast<std::chrono::microseconds>(end_phase1 - begin).count() << "\n";

		// cout << "TIME PHASE 2 = " << std::chrono::duration_cast<std::chrono::microseconds>(end - end_phase1).count() << "\n";
				cout << std::chrono::duration_cast<std::chrono::microseconds>(end - begin).count() << endl;

	}
};

int main(int argc, char *argv[]) {
	srand (55);

	int total_points, total_values, K, max_iterations, has_name;

	cin >> total_points >> total_values >> K >> max_iterations >> has_name;

	vector<Point> points;
	string point_name;

	for (int i = 0; i < total_points; i++) {
		vector<double> values;

		for (int j = 0; j < total_values; j++) {
			double value;
			cin >> value;
			values.push_back(value);
		}

		if (has_name) {
			cin >> point_name;
			Point p(i, values, K, point_name);
			points.push_back(p);
		} else {
			Point p(i, values, K);
			points.push_back(p);
		}
	}
	KMeans kmeans(K, total_points, total_values, max_iterations);
	kmeans.run(points);
	return 0;
}
