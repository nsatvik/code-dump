
def compute_prefix_func(pattern):
	m = len(pattern)
	pi = [0]*m
	for i in range(1,m):
		if pattern[pi[i-1]]==pattern[i]:
			pi[i]=pi[i-1]+1
	
	return pi

def find_pattern(text,pattern):
	n = len(text)
	m = len(pattern)
	pi = compute_prefix_func(pattern)
	print pi
	i = 0
	while i < n:
		j = 0
		while text[i] == pattern[j]:
			j+=1
			i+=1
			if j==m:
				return True
			if i==n:
				return false
		i = max(i+1,i+pi[j]+pi[j])
	return False

print find_pattern('This is a sample apple','apple')
print find_pattern('This is a sample apple','appappapp')
print find_pattern('This is a sample apple','ple')
print find_pattern('This is a sample apple',' appl')
print find_pattern('This is a sample apple','aaplel')


			
		
