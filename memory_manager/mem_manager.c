#include<stdio.h>
#include<ctype.h>
#include<stdlib.h>
#include<string.h>
#define LINE_INPUT_SIZE 256
#define APP_NAME_SIZE 16
#define ALLOC_REQ 1
#define FREE_REQ 0

#define RET_STATUS_SUC 1
#define RET_STATUS_FAIL 0

#define READ_APPNAME 4
#define READ_MEMSIZE 3
#define READ_REQ_TYPE 2

struct alloc_data {
	int start_loc;
	int end_loc;
	struct app_alloc_data * next;
};


/*
 * each mem unit.
 */
struct memory_unit {
    char app_name[APP_NAME_SIZE];
    int memory_size;
    struct memory_unit * next;
};

struct memory_unit * get_mem_unit(const char * app_name, const int req_size) {

    struct memory_unit * new_mem_unit = (struct memory_unit *) malloc(sizeof(struct memory_unit));
    if (new_mem_unit) {
	strcpy(new_mem_unit->app_name, app_name);
	new_mem_unit->memory_size = req_size;
	new_mem_unit->next = NULL;
    }
    return new_mem_unit;
}


struct memory_manager {
	int max_limit; //MAX limit that this mem can allocate.
	int cur_capacity;
	
	int num_application;
	struct alloc_data * alloc_apps_data;
	struct alloc_data * free_data;

	int num_misses;
	struct memory_unit * main_memory;
};

int my_malloc(struct memory_manager * ram_manager, const char * app_name, const int req_size) {
	struct memory_unit * root = ram_manager->main_memory;
	if ( ram_manager -> cur_capacity + req_size > ram_manager->max_limit) {
		ram_manager -> num_misses += 1;
		return RET_STATUS_FAIL;
	}
	struct memory_unit * new_alloc = get_mem_unit(app_name, req_size);
	ram_manager -> cur_capacity += req_size;
	new_alloc -> next = root;
	ram_manager->main_memory = root
	return RET_STATUS_SUC;
}

bool my_free(const memory_manager * manager, const char * appName, const int req) {
	struct memory_unit * root = ram_manager->main_memory;
	if (root == NULL) {
			return RET_STATUS_FAIL;
		}
		struct memory_unit * follower = NULL;
		struct memory_unit * iterator = root;
		while (iterator) {
			if (iterator -> memory_size == req_size && strcmp(iterator->app_name,app_name)==0) {
				break;
			}
		    follower = iterator;
			iterator = iterator -> next;
		}
		if (iterator != NULL) { //delete
			ram_manager->cur_capacity  -= req_size;
			if (follower == NULL) {
				//first node
				root = root->next;
				ram_manager->main_memory = root;
			} else {
				follower->next = iterator->next;
			}
			iterator->next = NULL;
			free(iterator);
			return RET_STATUS_SUC;
		} else {
			return RET_STATUS_FAIL;
		}
	}
}

void print_memory(const struct memory_manager *  ram_manager) {
	printf("MEMORY MANAGER STATS\n");
	printf("MAX_LIMIT : %d\n",ram_manager->max_limit);
    printf("MISSES : %d\n",ram_manager->num_misses);
	struct memory_unit * iter = ram_manager -> main_memory;
	while(iter) {
		printf("App Name: %s Mem : %d\n",iter->app_name,iter->memory_size);
		iter = iter->next;
	}
}

void process_file(const char * fileName);
void execute_request(const char * line);
int get_line_count(const char * fileName);

int main(int argc, char **argv) {

	if (argc != 3) {
		printf("Usage ./memory_manager allocation_seq_file allocation_file\n");
		exit(0);
	}


	process_file(argv[1]);

	// //init main memory
	// struct memory_manager m_manager;
	// m_manager.cur_capacity = 0;
	// m_manager.num_misses = 0;
	// m_manager.main_memory = NULL;

	// scanf("%d",&m_manager.max_limit);
	// const char app1[] = "A1";
	// const char app2[] = "A2";
	// my_malloc(&m_manager, app2, 10, ALLOC_REQ);
	// my_malloc(&m_manager, app1, 10, ALLOC_REQ);
	// //my_malloc(&m_manager, app2, 10, FREE_REQ);
	// //my_malloc(&m_manager, app1, 10, FREE_REQ);

	// print_memory(&m_manager);
}

int get_line_count(const char * fileName) {
	FILE * fp = fopen(fileName, "r");
	char ch;
	int count = 0;
	if (fp == NULL) {
		printf("File not found %s\n",fileName);
		exit(1);
	}

	while ((ch=fgetc(fp)) && ch != EOF) {
		if (ch == '\n') {
			count++;
		}
	}
	return count;
}

// memory manager as a param
void process_file(const char * fileName) {
	FILE * fp = fopen(fileName, "r");
	char ch ;
	int i = 0;
	char line[LINE_INPUT_SIZE] = {'\0'};
	if (fp == NULL) {
		printf("File not found %s\n",fileName);
		exit(1);
	}

	while ((ch=fgetc(fp)) && ch != EOF) {
		line[i++] = ch;
		if (ch == '\n') {
			line [i-1] = '\0';
			i = 0;
			execute_request(line);
		}
	}
}

void execute_request(const char * line) {
	char app_name[APP_NAME_SIZE] = {'\0'};

	int nam_index = 0;
	int req_size = 0;
	int req_type = -1;

	const char * alloc_func_name = "my_malloc()";
	const char * free_func_name = "my_free()";
	int state = READ_APPNAME;
	int processing = 1;
	for (int i=0; line[i] && processing; ++i) {
		while(line[i] != ':') {
			if (state == READ_APPNAME) {
				app_name[nam_index++] = line[i];
				if (line[i+1] && line[i+1] == ':') {
					state = READ_MEMSIZE;	
				}
			} else if (state == READ_MEMSIZE) {
				if (isdigit(line[i])) {
					req_size = req_size*10 + line[i]-'0';
				} else if (line[i+1] && line[i+1] == ':') {
					state = READ_REQ_TYPE;
				}
			} else if (state == READ_REQ_TYPE) {
				if (strcmp(&line[i],alloc_func_name) == 0 ) {
					//printf("allocate function\n");
					req_type = ALLOC_REQ;
					processing = 0;
					break;
				} else if (strcmp(&line[i], free_func_name) == 0) {
					//printf("free function\n");
					req_type = FREE_REQ;
					processing = 0;
					break;
				}
			} else {
				printf("Unexpected input error\n");
				exit(1);
			}
			++i;
		}
	}

	printf("App: %s Req: %d Type: %s\n",app_name,req_size, req_type==ALLOC_REQ?"allocate":"free");
}






