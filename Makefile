.PHONY: clean

COMPILER=mvn
FLAGS="-DskipTests"
TARGET=' clean package'

default: all

all: 
	$(COMPILER) $(FLAGS) $(TARGET)
	@echo
	@echo "See final ZIP artifact in target/"
	@echo
 
clean:
	$(COMPILER) clean
