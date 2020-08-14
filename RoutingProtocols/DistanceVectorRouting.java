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

public class DistanceVectorRouting {
	
	static int INF = (int)1e8;
	static int n, e;
	static ArrayList<Node> graph;
	static PriorityQueue<Node> pq = null;
	static LinkedList<String> fwdTable[];
	static int path[];
	static int parent[][] = new int[1000][1000];
	static int D[] = null;
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		n = sc.nextInt(); e = sc.nextInt();
		
		path = new int[n+1];
		
		graph = new ArrayList<Node>();
		fwdTable = new LinkedList[n+1];
		for(int i=1; i<=n; i++) {
			fwdTable[i] = new LinkedList<String>();
		}
		
		for(int i=1; i<=e; i++) {
			int u, v, wt;
			u = sc.nextInt(); v = sc.nextInt(); wt = sc.nextInt();
			graph.add(new Node(u, v, wt));
			graph.add(new Node(v, u, wt));
			parent[u][v] = -1;
			parent[v][u] = -1;
		}
		
		
		System.out.println("----------------");
		System.out.println("Network: ");
		
		for(Node n: graph)
			System.out.println(n);

		System.out.println("-----------------");
		
		System.out.println("Distances from start ");
		
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
		
		System.out.println("Enter the edge you want to update with the cost. For eg 1 2 15");
		int up1 = sc.nextInt(), up2 = sc.nextInt(), up3 = sc.nextInt();

		//updating cost in original graph
		int pos = 0;
		for(Node n: graph) {
			if(n.u==up1 && n.v==up2) {
				graph.set(pos, new Node(up1, up2, up3));
			}
			pos++;
		}
		
		pos = 0;
		for(Node n: graph) {
			if(n.u==up2 && n.v==up1) {
				graph.set(pos, new Node(up2, up1, up3));
			}
			pos++;
		}
		System.out.println("Updated network");
		
		for(Node n: graph)
			System.out.println(n);
		
		for(int i=1; i<=n; i++) {
			fwdTable[i] = new LinkedList<String>();
		}
		
		System.out.println("-----------------");
		
		
		System.out.println("Updated paths and costs");
		
		path = new int[n+1];
		
		for(int i=1; i<=n; i++) {
			for(int j=1; j<=n; j++) {
				if(i!=j) {
					setup(i, j);
				}
			}
			for(int k=0; k<100; k++)
				for(int l=0; l<100; l++) {
					parent[k][l] = -1;
					parent[l][k] = -1;
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
		
		bellmanford(start, end);
		
	    int cur = end, t;
	    Stack<Integer> ans = new Stack<>();
	    while(cur!=0) 
	    {
	        ans.push(cur);
	        cur = path[cur];
	    }
	    
	    System.out.println("Source vertex " + start);
	    
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
	    	//System.out.print(t + "->");
	    }
	    t = ans.peek();
    	ans.pop();

    	for(int i=1; i<=n; i++) {
    		System.out.println(i + "->" + D[i]);
    	}
    	
	    System.out.println();
	}
	
	static void bellmanford(int start, int end) {
		D = new int[n+1];
		for(int i = 1; i <= n; i++) {
			D[i] = INF;
			path[i] = 0;
		}
		D[start] = 0;
		for (int i=1; i<=n; i++)
		{
			for(Node n: graph) {
				int u = n.u;
				int v = n.v;
				int wt = n.wt;
				//poison reverse
			    if(parent[i][v] == u)
			    	wt = INF;
				
				if((D[u]+wt < D[v])) {
					D[v] = D[u] + wt;
					System.out.println("Updating cost of " + u + "->" + v + " with wt " + D[v]);
					path[v] = u;
					parent[start][v] = i;
				}
			}
        }
	}
}
