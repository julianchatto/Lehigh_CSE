// http://www.cplusplus.com/reference/ctime/time/ is helpful here
#include <deque>
#include <iostream>
#include <memory>
#include <mutex>
#include <ctime>

#include "quota_tracker.h"

using namespace std;

/// quota_tracker stores time-ordered information about events.  It can count
/// events within a pre-set, fixed time threshold, to decide if a new event can
/// be allowed without violating a quota.
class my_quota_tracker : public quota_tracker {

/// struct of an event with timestamp and amount
struct event {
    time_t when; 
    size_t amnt;
};

private:
    size_t q_amnt;
    double q_dur; 
    deque<event> events; 
    mutex lock; 

public:
	/// Construct a tracker that limits usage to quota_amount per quota_duration
	/// seconds
	///
	/// @param amount   The maximum amount of service
	/// @param duration The time over which the service maximum can be spread out
	my_quota_tracker(size_t amount, double duration) : q_amnt(amount), q_dur(duration) {}

	/// Destruct a quota tracker
	virtual ~my_quota_tracker() {
		lock_guard<mutex> guard(lock);
        events.clear();
	}

	/// Decide if a new event is permitted, and if so, add it.  The attempt is
	/// allowed if it could be added to events, while ensuring that the sum of
	/// amounts for all events within the duration is less than q_amnt.
	///
	/// @param amount The amount of the new request
	///
	/// @return false if the amount could not be added without violating the
	///         quota, true if the amount was added while preserving the quota
	virtual bool check_add(size_t amount) {
		lock_guard<mutex> guard(lock);

        // gets current time
        time_t current_time = time(nullptr); // nullptr means that it does not need to be saved 

        // remove expired events outside q_dur window
        while (!events.empty() && difftime(current_time, events.front().when) > q_dur) {
            events.pop_front();
        }

        // calculate current total usage within q_dur window
        size_t total_usage = 0;
        for (const auto& evt : events) {
            total_usage += evt.amnt;
        }

        // check if adding new amount exceeds quota
        if ((total_usage + amount) > q_amnt) {
            return false; // quota exceeded, deny request
        }

        // add new event
        events.push_back({current_time, amount});
        return true;
	}
};

/// Construct a tracker that limits usage to quota_amount per quota_duration
/// seconds
///
/// @param amount   The maximum amount of service
/// @param duration The time over which the service maximum can be spread out
quota_tracker *quota_factory(size_t amount, double duration) {
  	return new my_quota_tracker(amount, duration);
}