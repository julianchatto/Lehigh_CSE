# Build a client and server using only the student's my_my_storage.cc

# The executables will have the suffix .exe
EXESUFFIX = exe

# Names for building the client:
CLIENT_MAIN     = client
CLIENT_CXX      = 
CLIENT_COMMON   = 
CLIENT_PROVIDED = client requests crypto err file net my_crypto

# Names for building the server
SERVER_MAIN     = server
SERVER_CXX      = my_mapreduce
SERVER_COMMON   = 
SERVER_PROVIDED = server responses parsing concurrentmap_factories crypto \
                  err file net my_crypto my_gatekeeper my_pool my_mru     \
                  my_functable persist my_quota_tracker my_storage

# Pull in the common build rules
include common.mk
