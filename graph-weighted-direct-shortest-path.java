// https://leetcode.com/problems/design-graph-with-shortest-path-calculator/description/?envType=daily-question&envId=2023-11-11

// Dijkstra's algorithm, a well-known method for finding the shortest path in weighted graphs,

import java.util.List;
import java.util.PriorityQueue;

class Graph {
    private List<int[]>[] adj;

    public Graph(int n, int[][] edges) {
        adj = new LinkedList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new LinkedList<>();
        }
        for (int[] is : edges) {
            adj[is[0]].add(new int[] {is[1], is[2]});
        }
    }
    
    public void addEdge(int[] edge) {
        adj[edge[0]].add(new int[] {edge[1], edge[2]});
    }
    
    public int shortestPath(int node1, int node2) {
        // pair -> [min cost from src node to current node, current node index]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(pair->pair[0]));

        // src node to each node's cost
        int[] costArray = new int[adj.length];

        Arrays.fill(costArray, Integer.MAX_VALUE);
        costArray[node1] = 0;
        pq.offer(new int[] {0, node1});

        while (!pq.isEmpty()) {
            int[] pair = pq.poll();
            int currCost = pair[0];
            int currNode = pair[1];

            // for already visited node, if this step's cost is bigger, then stop visiting it
            if (currCost > costArray[currNode]) {
                continue;
            }

            // the element in queue, is the cumulated cost to the node, so if found dest return it directly
            if (currNode == node2) {
                return currCost;
            }

            for (int[] neighbor : adj[currNode]) {
                int neighborNode = neighbor[0];
                int cost = neighbor[1];
                int newCost = currCost + cost;

                if (newCost < costArray[neighborNode]) {
                    costArray[neighborNode] = newCost;
                    pq.offer(new int[] {newCost, neighborNode});
                }
            }
        }

        return -1;
    }
}

// Floyd–Warshall algorithm
class Graph2 {
    private int[][] adjMatrix;

    public Graph(int n, int[][] edges) {
        adjMatrix = new int[n][n];
        Arrays.stream(adjMatrix).forEach(row -> Arrays.fill(row, (int)1e9));
        for (int[] e : edges) {
            adjMatrix[e[0]][e[1]] = e[2];
        }
        for (int i = 0; i < n; i++) {
            adjMatrix[i][i] = 0;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    adjMatrix[j][k] = Math.min(adjMatrix[j][k], 
                                               adjMatrix[j][i] +
                                               adjMatrix[i][k]);
                }
            }
        }
    }

    public void addEdge(int[] edge) {
        int n = adjMatrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjMatrix[i][j] = Math.min(adjMatrix[i][j],
                                           adjMatrix[i][edge[0]] +
                                           adjMatrix[edge[1]][j] +
                                           edge[2]);
            }
        }
    }

    public int shortestPath(int node1, int node2) {
        if (adjMatrix[node1][node2] == (int)1e9)
            return -1;
        return adjMatrix[node1][node2];
    }
}

/**
 * Your Graph object will be instantiated and called as such:
 * Graph obj = new Graph(n, edges);
 * obj.addEdge(edge);
 * int param_2 = obj.shortestPath(node1,node2);
 */