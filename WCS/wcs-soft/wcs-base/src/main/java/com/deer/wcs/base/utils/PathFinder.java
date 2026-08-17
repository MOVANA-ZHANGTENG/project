package com.deer.wcs.base.utils;

import java.util.*;

public class PathFinder {
    public static void main(String[] args) {
        List<StationRelation> relationList=  new ArrayList<>();
        relationList.add(new StationRelation("A","B",1));
        relationList.add(new StationRelation("A","C",2));
        relationList.add(new StationRelation("A","F",1));
        relationList.add(new StationRelation("B","D",5));
        relationList.add(new StationRelation("C","E",3));
        relationList.add(new StationRelation("E","D",4));
        relationList.add(new StationRelation("F","D",1));


        List<String> path =  findShorPath("A","D",relationList);
        for (String station : path) {
            System.out.println(station);
        }
    }

    /**
     * 寻找最短路径
     * @param start 开始节点
     * @param end   目标节点
     * @param relations  相邻节点之间的关系
     * @return
     */
    public static List<String> findShorPath(String start, String end,List<StationRelation> relations) {
        // 初始化距离和父节点映射
        Map<String, Integer> distances = new HashMap<>();
        //previous代表走过的路径
        Map<String, String> previous = new HashMap<>();

        for (StationRelation relation : relations) {
            //初始化距离：[{起点1，无穷远},{起点2，无穷远}]
            if(!distances.containsKey(relation.getFromStationCode())){
                distances.put(relation.getFromStationCode(), Integer.MAX_VALUE);
            }
            //初始化距离：[{终点1，无穷远},{终点1，无穷远}]
            if(!distances.containsKey(relation.getToStationCode())){
                distances.put(relation.getToStationCode(), Integer.MAX_VALUE);
            }

        }
        // 起始点到自身的距离为0,此时除了点start->1距离为0，其他的点位都默认为无穷远
        distances.put(start, 0);

        // 使用优先队列来保存待处理的站点，按距离排序
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            // 检查当前站点是否为目标站点
            if (current.equals(end)) {
                break;
            }

            // 遍历当前站点的所有关系
            for (StationRelation relation : relations) {
                if (relation.getFromStationCode().equals(current)) {
                    //当前点可能到达的下一个点的信息,以及从起始点到该点位的距离
                    String neighbor = relation.getToStationCode();
                    int newDistance = distances.get(current) + relation.getWeight();

                    // 如果找到了更短的路径，则更新距离和父节点信息
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        previous.put(neighbor, current);

                        // 将邻居站点加入队列（如果它还不在队列中）
                        if (!queue.contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        // 重构路径
        List<String> path = new ArrayList<>();
        while (end != null) {
            path.add(end);
            end = previous.get(end);
        }
        Collections.reverse(path); // 路径是从终点到起点的，所以需要反转

        if(path.size()<2){
            throw  new RuntimeException("未找到路径");
        }
        return path;
    }
}


// 站点关系类
class StationRelation {
    private String fromStationCode;
    private String toStationCode;
    private int weight; // 假设权重是一个整数

    public StationRelation(String fromStationCode, String toStationCode, int weight) {
        this.fromStationCode = fromStationCode;
        this.toStationCode = toStationCode;
        this.weight = weight;
    }

    public String getFromStationCode() {
        return fromStationCode;
    }

    public void setFromStationCode(String fromStationCode) {
        this.fromStationCode = fromStationCode;
    }

    public String getToStationCode() {
        return toStationCode;
    }

    public void setToStationCode(String toStationCode) {
        this.toStationCode = toStationCode;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}


