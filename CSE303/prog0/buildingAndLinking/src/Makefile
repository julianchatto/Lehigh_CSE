# The name of the executable, and the names of the C++ files we use to build it
TARGET   = prog
CXXFILES = prog

# Location of the base folder where our custom-generated .so object can be found
LIB_PATH="../lib"

# Basic tool configuration for a 64-bit build
BITS     ?= 64
CXX       = g++
LD        = g++
CXXFLAGS += -MMD -O3 -m$(BITS) -ggdb -std=c++17 -Wall -Werror -march=native
LDFLAGS  += -m$(BITS)

# Special flags for using our custom-generated .so file
CXXFLAGS += -I $(LIB_PATH)
LDFLAGS  += -L $(LIB_PATH)/$(ODIR) -lmylib

# Compute the names of everything that gets built; make the output folder
ODIR       := ./obj$(BITS)
out_folder := $(shell mkdir -p $(ODIR))
EXEFILES    = $(patsubst %, $(ODIR)/%.exe, $(TARGET))
OFILES      = $(patsubst %, $(ODIR)/%.o, $(CXXFILES))
DFILES      = $(patsubst %, $(ODIR)/%.d, $(CXXFILES))

# Build 'all' by default, and don't clobber .o files after each build
.DEFAULT_GOAL = all
.PRECIOUS: $(OFILES)
.PHONY: all clean

# Goal is to build the executable
all: $(EXEFILES)

# Build a .o from a .cc
$(ODIR)/%.o: %.cc
	@echo "[CXX] $< --> $@"
	@$(CXX) $< -o $@ -c $(CXXFLAGS)

# Build a .exe from all the .o files
$(ODIR)/%.exe: $(OFILES)
	@echo "[LD] $^ --> $@"
	@$(LD) $^ -o $@ $(LDFLAGS)

# Clean by clobbering the build folder
clean:
	@echo Cleaning up...
	@rm -rf $(ODIR)

# Include the auto-generated dependency files, so that re-making is quick
-include $(DFILES)