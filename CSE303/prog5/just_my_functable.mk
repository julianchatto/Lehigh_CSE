# The executables will have the suffix .exe
EXESUFFIX = exe

# Names for building the client:
CLIENT_MAIN     = client
CLIENT_CXX      = 
CLIENT_COMMON   = 
CLIENT_PROVIDED = client requests crypto err file net my_crypto

# Names for building the server
SERVER_MAIN     = server
SERVER_CXX      = my_functable
SERVER_COMMON   = 
SERVER_PROVIDED = server responses parsing persist concurrentmap_factories \
                  crypto err file net my_pool my_crypto my_quota_tracker   \
                  my_mru my_storage my_mapreduce my_gatekeeper

# Pull in the common build rules
include common.mk
