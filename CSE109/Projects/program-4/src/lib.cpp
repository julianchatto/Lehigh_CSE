#include <vector>
#include <string.h>
#include "pack109.hpp"

// bool
vec pack109::serialize(bool item) {
	vec bytes;
	if (item == true) { // pushback based on value of item
		bytes.push_back(PACK109_TRUE);
	} else {
		bytes.push_back(PACK109_FALSE);
	}
	return bytes;
}
bool pack109::deserialize_bool(vec bytes) {
	if (bytes.size() != 1) { // error checking
		throw;
	}
	if (bytes[0] == PACK109_TRUE) { // return based on value of bytes[0]
		return true;
	} else if (bytes[0] == PACK109_FALSE) {
		return false;
	} 
	throw;
}

// u8
vec pack109::serialize(u8 item) {
	vec bytes;
	bytes.push_back(PACK109_U8); // push tag
	bytes.push_back(item); // push item
	return bytes;
}
u8 pack109::deserialize_u8(vec bytes) {
	if (bytes.size() != 2) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_U8) { // return based on value of bytes[0]
		throw;
	} 
	return bytes[1];	
}

void pack109::printVec(vec &bytes) {
	printf("[ ");
	for (int i = 0; i < bytes.size(); i++) {
		printf("%x ", bytes[i]);
	}
	printf("]\n");
}

// u32
vec pack109::serialize(u32 item) {
	vec bytes;
	bytes.push_back(PACK109_U32); // push tag
	for (int i = 3; i >= 0; i--) { // push item
		bytes.push_back(item >> (i*8)); // shifting to get each byte onto vec
	}
	return bytes;
}
u32 pack109::deserialize_u32(vec bytes) {
	if (bytes.size() != 5) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_U32) { // error checking
		throw;
	}
	u32 temp = 0; 
	for (int i = 3, j = 1; i >= 0; i--, j++) { // shifting to get each byte temp
		temp |= (bytes[j] << (i*8)); // shifting to get each byte onto temp
	}
	return temp;

}

// u64
vec pack109::serialize(u64 item) {
	vec bytes;
	bytes.push_back(PACK109_U64); // push tag
	for (int i = 7; i >= 0; i--) { // push item 
		bytes.push_back(item >> (i * 8)); // shifting to get each byte onto vec
	}
	return bytes;
}
u64 pack109::deserialize_u64(vec bytes) {
	if (bytes.size() != 9) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_U64) { // error checking
		throw;
	}
	u64 temp = 0; 
	for (int i = 7, j = 1; i >= 0; i--, j++) { // shifting to get each byte onto temp
		temp |= (bytes[j] << i * 8); 
	}
	return temp;
}

// i8
vec pack109::serialize(i8 item) {
	vec bytes;
	bytes.push_back(PACK109_I8); // push tag
	bytes.push_back(item); // push item
  	return bytes;
}
i8 pack109::deserialize_i8(vec bytes) {
	if (bytes.size() != 2) {
		throw;
	}
	if (bytes[0] != PACK109_I8) {
		throw;
	}
	return bytes[1];
}

// i32
vec pack109::serialize(i32 item) {
	vec bytes;
	bytes.push_back(PACK109_I32); // push tag
	for (int i = 3; i >= 0; i--) { // push item
		bytes.push_back(item >> (i*8)); // shifting to get each byte onto vec
	}
	return bytes;
}
i32 pack109::deserialize_i32(vec bytes) {
	if (bytes.size() != 5) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_I32) { // error checking
		throw;
	}
	i32 temp = 0; 
	for (int i = 3, j = 1; i >= 0; i--, j++) { // shifting to get each byte temp
		temp |= (bytes[j] << (i*8)); // shifting to get each byte onto temp
	}
	return temp;
}

// i64
vec pack109::serialize(i64 item) {
	vec bytes;
	bytes.push_back(PACK109_I64); // push tag
	for (int i = 7; i >= 0; i--) { // push item 
		bytes.push_back(item >> (i * 8)); // shifting to get each byte onto vec
	}
	return bytes;
}
i64 pack109::deserialize_i64(vec bytes) {
	if (bytes.size() != 9) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_I64) { // error checking
		throw;
	}
	i64 temp = 0; 
	for (int i = 7, j = 1; i >= 0; i--, j++) { // shifting to get each byte onto temp
		temp |= (bytes[j] << i * 8); 
	}
	return temp;
}

// Floats
// f32
vec pack109::serialize(f32 item) {
	vec bytes;
	bytes.push_back(PACK109_F32); // push tag
	u32* temp = (u32*) &item;  // u32 array to hold the bytes of item
	for (int i = 3; i >= 0; i--) { // push item
		bytes.push_back(*temp >> (i * 8));
	}
	return bytes;
}
f32 pack109::deserialize_f32(vec bytes) {
	if (bytes.size() != 5) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_F32) { // error checking
		throw;
	}
	// convert to little endian	
	vec tempVec{PACK109_F32};
	for (int i = bytes.size() - 1; i >= 1; i--) {
		tempVec.push_back(bytes[i]);
	}
	// end convert
	f32 temp;
	memcpy(&temp, tempVec.data() + 1, sizeof(f32)); // copy bytes into temp
	return temp;
}

// f64
vec pack109::serialize(f64 item) {
	vec bytes;
	bytes.push_back(PACK109_F64); // push tag
	u64* temp = (u64*) &item; // u64 array to hold the bytes of item
	for (int i = 7; i >= 0; i--) { // push item
		bytes.push_back(*temp >> (i * 8)); // shifting to get each byte onto vec
	}
	return bytes;
}
f64 pack109::deserialize_f64(vec bytes) {
	if (bytes.size() != 9) { // error checking
		throw;
	}
	if (bytes[0] != PACK109_F64) { // error checking
		throw;
	}
	// convert to little endian	
	vec tempVec{PACK109_F64};
	for (int i = bytes.size() - 1; i >= 1; i--) {
		tempVec.push_back(bytes[i]);
	}
	// end convert
	f64 temp;
	memcpy(&temp, tempVec.data() + 1, sizeof(f64)); // copy bytes into temp
	return temp;
}

// Strings
vec pack109::serialize(string item) {
	vec bytes;
	int count = item.size();
	if (count > 65535) { // error check
		throw;
	}
	if (count < 256) { // S8
		bytes.push_back(PACK109_S8); // push tag
		bytes.push_back((u8) count); // push count
	} else { // S16
		bytes.push_back(PACK109_S16); // push tag
		bytes.push_back((u8) 255); // push 255
		bytes.push_back((u8) count - 255); // push remainder
	}
	// Push string
	for (int i = 0 ; i < count; i++) {
		bytes.push_back(item[i]);
	}
	return bytes;
}
string pack109::deserialize_string(vec bytes) {
	if (bytes.size() < 3) { // error checking
		throw;
	}
	int upperBound = bytes[1] + 2; // s8 
	int lowerBound = 2;
	if (bytes[0] == PACK109_S16) { // if s16
		upperBound += bytes[2] + 1;
		lowerBound++;
	} else if(bytes[0] != PACK109_S8) { // neither s8 nor s16
		throw;
	}
	string temp = ""; 
	for (int i = lowerBound; i < upperBound; i++) { // push bytes of string
		temp += bytes[i];
	}
	return temp;
}
  
// Arrays
// u8
vec pack109::serialize(std::vector<u8> item) {
	vec bytes; 
	int count = item.size(); // count of items
	if (count < 256) { 
		bytes.push_back(PACK109_A8); // push tag
		bytes.push_back((u8) count); // push count
	} else if (count < 65536) {
		bytes.push_back(PACK109_A16); // push tag
		bytes.push_back((u8) 255); // push 255
		bytes.push_back((u8) count - 255); // push remainder
	} else { 
		throw;
	}
	// Push items
	for (int i = 0; i < item.size(); i++) {
		bytes.push_back(PACK109_U8);
		bytes.push_back(item[i]);
	}
	return bytes;

}
std::vector<u8> pack109::deserialize_vec_u8(vec bytes) {
	if (bytes.size() < 3 || (bytes[0] != PACK109_A8 && bytes[0] != PACK109_A16)) { // error checking
		throw;
	} 
	std::vector<u8> temp;
	int startVal = 2; // if it is A8
	if (bytes[0] == PACK109_A16) { // if it is A16
		startVal++;
	}
	// push bytes
	for (int i = startVal; i < bytes.size(); i++) {
		if (bytes[i] == PACK109_U8) {
			continue;
		}
		temp.push_back(bytes[i]);
	}
	return temp;
}

// u64
vec pack109::serialize(std::vector<u64> item) {
	vec bytes; 
	int count = item.size(); // count of items 
	if (count < 256) { 
		bytes.push_back(PACK109_A8); // push tag
		bytes.push_back((u8) count); // push count
	} else if (count < 65536) {
		bytes.push_back(PACK109_A16); // push tag
		bytes.push_back((u8) 255); // push 255
		bytes.push_back((u8) count - 255); // push remainder
	} else { 
		throw;
	}
	// Push items
	for (int i = 0; i < item.size(); i++) {
		vec temp = serialize(item[i]); // serialize the item
		// push temp 
		for (int j = 0; j < temp.size(); j++) {
			bytes.push_back(temp[j]);
		}
	}
	return bytes;
}
std::vector<u64> pack109::deserialize_vec_u64(vec bytes) {
	if (bytes.size() < 3 || (bytes[0] != PACK109_A8 && bytes[0] != PACK109_A16)) { // error checking
		throw;
	} 
	std::vector<u64> returnVec;
	int startVal = 2; // if it is A8
	if (bytes[0] == PACK109_A16) { // if it is A16
		startVal++;
	}
	// push bytes
	for (int i = startVal; i < bytes.size();) {
		if (bytes[i] == PACK109_U64) { // if it is a u64, push the tag and continue
			i++;
			continue;
		}
		vec temp{PACK109_U64};
		// push bytes to temp
		for (int j = i; j < i + 8; j++) {
			temp.push_back(bytes[j]);
		}
		u64 temp_u64 = deserialize_u64(temp); // deserialize the u64
		returnVec.push_back(temp_u64);
		i+=8;
	}
	return returnVec; 
}	

// f64
vec pack109::serialize(std::vector<f64> item) {
	vec bytes; 
	int count = item.size(); // count of items
	if (count < 256) {
		bytes.push_back(PACK109_A8); // push tag
		bytes.push_back((u8) count); // push count
	} else if (count < 65536) { 
		bytes.push_back(PACK109_A16); // push tag
		bytes.push_back((u8) 255); // push 255
		bytes.push_back((u8) count - 255); // push remainder
	} else { 
		throw;
	}
	for (int i = 0; i < item.size(); i++) { // push items
		vec temp = serialize(item[i]); // serialize the item
		for (int j = 0; j < temp.size(); j++) { // push temp
			bytes.push_back(temp[j]);
		}
	}
	return bytes;
}	
std::vector<f64> pack109::deserialize_vec_f64(vec bytes) {
	if (bytes.size() < 3 || (bytes[0] != PACK109_A8 && bytes[0] != PACK109_A16)) { // error checking
		throw;
	} 
	std::vector<f64> returnVec;
	int startVal = 2; // if it is A8
	if (bytes[0] == PACK109_A16) { // if it is A16
		startVal++;
	}

	for (int i = startVal; i < bytes.size();) { // push bytes
		if (bytes[i] == PACK109_F64) { // if it is a f64
			i++;
			continue;
		}
		vec temp{PACK109_F64};
		for (int j = i ; j < i + 8; j++) { // push bytes to temp
			temp.push_back(bytes[j]);
		}
		f64 temp_f64 = deserialize_f64(temp); // deserialize the f64
		returnVec.push_back(temp_f64);
		i+=8;
	}
	return returnVec; 
}

// string 
vec pack109::serialize(std::vector<string> item) {
	vec bytes; 
	int count = item.size(); // count of items
	if (count < 256) {
		bytes.push_back(PACK109_A8); // push tag
		bytes.push_back((u8) count); // push count
	} else if (count < 65536) {
		bytes.push_back(PACK109_A16); // push tag
		bytes.push_back((u8) 255); // push 255
		bytes.push_back((u8) count - 255); // push remainder
	} else { 
		throw;
	}
	for (int i = 0; i < item.size(); i++) { // push items
		vec temp = serialize(item[i]); // serialize the item
		for (int j = 0; j < temp.size(); j++) { // push temp
			bytes.push_back(temp[j]);
		}
	}
	return bytes;
}
std::vector<string> pack109::deserialize_vec_string(vec bytes) {
	if (bytes.size() < 3 || (bytes[0] != PACK109_A8 && bytes[0] != PACK109_A16)) { // error check
		throw;
	}
	std::vector<string> returnVec;
	int startVal = 2; // if it is A8
	if (bytes[0] == PACK109_A16) { // if it is A16
		startVal++;
	}

	for (int i = startVal; i < bytes.size();) { // push bytes 
		vec temp;
		int upperBound = bytes[i+1] + 2; // determine how long the string is for s8
		if (bytes[i] == PACK109_S16) { // determine how long the string is for s16
			upperBound += bytes[i+2] + 1;
		} 
		for (int j = i; j < i + upperBound; j++) { // push bytes to temp
			temp.push_back(bytes[j]);
		}
		string temp_f64 = deserialize_string(temp); // deserialze temp
		returnVec.push_back(temp_f64); // push to returnVec
		i += upperBound;
	}
	return returnVec; 
}

// Maps
vec pack109::serialize(struct Person item) {
	vec bytes;
	// Push map tag
	bytes.push_back(PACK109_M8);
	bytes.push_back(0x01);
	
	// Specify struct type 
	{
		string st = "Person";
		vec temp = serialize(st); // serialize person
		for (int i = 0; i < temp.size(); i++) { // push to bytes
			bytes.push_back(temp[i]);
		}
	}
	
	// Specify the number of attributes for struct
	bytes.push_back(PACK109_M8);
	bytes.push_back(0x03);

	//Begin K/V

	// Pair 1
	// Key
	{
		string st = "age";
		vec temp = serialize(st); // serialize age
		for (int i = 0; i < temp.size(); i++) { // push to bytes
			bytes.push_back(temp[i]);
		}
	}

	// Value
	bytes.push_back(PACK109_U8);
	bytes.push_back(item.age); // push age

	// Pair 2
	// Key
	{
		string st = "height";
		vec temp = serialize(st); // serialize height
		for (int i = 0; i < temp.size(); i++) { // push to bytes
			bytes.push_back(temp[i]);
		}
	}
	
	// Value
	vec heightVal = serialize(item.height); // serialize height
	for (int i = 0; i < heightVal.size(); i++) { // push to bytes
		bytes.push_back(heightVal[i]);
	}

	// Pair 3
	// Key
	{
		string st = "name";
		vec temp = serialize(st); // serialize name
		for (int i = 0; i < temp.size(); i++) { // push to bytes 
			bytes.push_back(temp[i]);
		}
	}

	// Value
	{
		string st = item.name;
		vec temp = serialize(st); // serialize name
		for (int i = 0; i < temp.size(); i++) { // push to bytes
			bytes.push_back(temp[i]);
		}
	}
	return bytes;
}
struct Person pack109::deserialize_person(vec bytes) {
	if (bytes.size() < 3 || (bytes[0] != PACK109_M8 && bytes[0] != PACK109_M16)) { // error check
		throw;
	}
	Person newPers; 
	u8 age;
	int i = 0;
	while (bytes[i] != PACK109_U8) { // advancing i to the age
		i++;
	}

	age = (u8) bytes[i + 1]; // reading age
	while(bytes[i] != PACK109_F32) { // advancing i to the height
		i++;
	}

	vec tempHeight;
	for (; bytes[i] != PACK109_S8; i++) { // reading height
		tempHeight.push_back(bytes[i]);
	}
	i++;

	while(bytes[i] != PACK109_S8) { // advancing i to the names
		i++;
	}

	while(bytes[i] != PACK109_S8) { // previous one advances to the beginning of the key this advances to the value
		i++;
	}

	vec tempName;
	for (; i < bytes.size(); i++) { // reading name
		tempName.push_back(bytes[i]);
	}
	
	// setting age, height, name
	newPers.age = (char) age;
	newPers.height = (float) deserialize_f32(tempHeight);
	newPers.name = deserialize_string(tempName);
	return newPers;
}