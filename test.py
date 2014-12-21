import sys

def main():
  for arg in sys.argv:
    print arg
  print 'length = ',len(sys.argv)
  
  print 'done'


if __name__=="__main__":
  main()
