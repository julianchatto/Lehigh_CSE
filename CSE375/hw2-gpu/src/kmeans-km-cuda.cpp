#include <iostream>
#include <vector>
#include <chrono>
#include <string>    
#include "kmcuda.h"

using namespace std;

int main(int argc, char *argv[]) {
    // load data
    int total_points, total_values, K, max_iterations, has_name;
    cin >> total_points >> total_values >> K >> max_iterations >> has_name;

    vector<float> dataset(total_points * total_values);
    for (int i = 0; i < total_points; i++) {
        for (int j = 0; j < total_values; j++) {
            float value;
            cin >> value;
            dataset[i * total_values + j] = value;
        }
        if (has_name) {
            string s;
            cin >> s;
        }
    }

    vector<float> centroids(K * total_values, 0.0f);
    vector<uint32_t> assignments(total_points, 0);
    
    auto start = chrono::high_resolution_clock::now();
    KMCUDAResult result = kmeans_cuda(
        kmcudaInitMethodRandom,
        nullptr,
        1e-3f,                    // tolerance
        0.1f,                     // yinyang_t
        kmcudaDistanceMetricL2,   // metric
        static_cast<uint32_t>(total_points),
        static_cast<uint32_t>(total_values),
        static_cast<uint32_t>(K),
        55,                       // seed
        1,                        // device
        -1,                        // device pointer
        0,                        // 
        0,                        // 
        dataset.data(),
        centroids.data(),
        assignments.data(),
        nullptr                   // distance_per_sample = NULL
    );
    auto end = chrono::high_resolution_clock::now();

    if (result != kmcudaSuccess) {
        cerr << "kmeans_cuda failed with code " << result << endl;
        return 1;
    }

    cout  << chrono::duration_cast<chrono::microseconds>(end - start).count() << endl;

    return 0;
}
