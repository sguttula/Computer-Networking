import java.io.*;
import java.util.*;

class Node {
	int u, v, wt;

	public Node(int u, int v, int wt) {
		this.u = u;
		this.v = v;
		this.wt = wt;
	}
	@Override
	public String toString() {
		return "Node [u=" + u + ", v=" + v + ", wt=" + wt + "]";
	}
	public int getV() {
		return v;
	}
	public int getWt() {
		return wt;
	}
	public int getU() {
		return u;
	}
}

class WeightComparator implements Comparator<Node> {
	
    @Override
    public int compare(Node x, Node y)
    {
        if (x.wt < y.wt) {
            return -1;
        }
        if (x.wt > y.wt) {
            return 1;
        }
        return 0;
    }
}

public class LinkStateRoutingDijkstras {
	
	static int INF = (int)1e8;
	static int n, e;
	static PriorityQueue<Node> pq = null;
	static LinkedList<Node> graph[];
	static LinkedList<String> fwdTable[];
	static int path[];
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		n = sc.nextInt(); e = sc.nextInt();
		
		path = new int[n+1];
		
		graph = new LinkedList[n+1];
		fwdTable = new LinkedList[n+1];
		for(int i=1; i<=n; i++) {
			graph[i] = new LinkedList<Node>();
			fwdTable[i] = new LinkedList<String>();
		}
		
		for(int i=1; i<=e; i++) {
			int u, v, wt;
			u = sc.nextInt(); v = sc.nextInt(); wt = sc.nextInt();
			graph[u].add(new Node(u, v, wt));
			graph[v].add(new Node(v, u, wt));
		}
		
		
		System.out.println("----------------");
		System.out.println("Network: ");
		for(int i=1; i<=n; i++) {
			for(Node n: graph[i]) {
				System.out.println(n);
			}
		}
		System.out.println("-----------------");
		
		System.out.println("Paths from start -> end nodes");
		
		for(int i=1; i<=n; i++) {
			for(int j=1; j<=n; j++) {
				if(i!=j) {
					setup(i, j);
				}
			}
		}
		
		System.out.println("-----------------");
		
		for(int i=1; i<=n; i++) {
			System.out.println("Least Cost Path Forwarding table for node " + i);
			for(String s:fwdTable[i]) {
				System.out.println(s);
			}
			System.out.println("-----------------");
		}
		

 	}
	
	static void setup(int start, int end) {
		
		dijkstra(start, end);
		
	    int cur = end;
	    Stack<Integer> ans = new Stack<>();
	    while(cur!=0) 
	    {
	        ans.push(cur);
	        cur = path[cur];
	    }
	    int t;
	    System.out.println("Path from " + start +" -> " + end + ":");
	    
	    //Creating forwarding table
	    if(ans.size()>1) {
	    	int t1 = ans.peek();
	    	ans.pop();
	    	int t2 = ans.peek();
	    	ans.push(t1);
	    	fwdTable[start].add(end + "\t" + "(" + t1 + "," + t2 + ")");
	    }
	    while(ans.size()!=1) {
	    	t = ans.peek();
	    	ans.pop();
	    	System.out.print(t + "->");
	    }
	    t = ans.peek();
    	ans.pop();
    	System.out.print(t);
    	
	    System.out.println();	
	}
	
	static void dijkstra(int start, int end) {
	    
		int D[] = new int[n+1];
		for(int i = 1; i <= n; i++) {
			D[i] = INF;
			path[i] = 0;
		}
	    D[start] = 0;
		Comparator<Node> comparator = new WeightComparator();
		pq = new PriorityQueue<>(comparator);
		
		pq.add(new Node(0, start, 0));
		
		while(!pq.isEmpty()) {
			Node p = pq.poll();
			int u = p.v;
			int dist = p.wt;
			
	        if(dist > D[u]) 
	            continue;
	        
	        for(Node pr: graph[u]) {
	        	int v = pr.getV();
	        	int next_dist = dist + pr.getWt();
	            if(next_dist < D[v]) {
	            	D[v] = next_dist;
	            	pq.add(new Node(0, v, next_dist));
	            	path[v] = u;
	            	System.out.println("Updating cost from " + u + " to " + v + "  with wt " + D[v]);
	            }
	        }
	        if(u==end)
	            break;
		}
	}
}