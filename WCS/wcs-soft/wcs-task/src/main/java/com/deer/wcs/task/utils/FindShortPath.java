package com.deer.wcs.task.utils;

import com.deer.wcs.base.model.PositionCondition;
import com.deer.wcs.task.model.JobInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 寻找最短路径
 * @author:zfj
 * @date:2024/5/14 15:40
 */
public class FindShortPath {

    public static void main(String[] args) {
        List<PositionCondition> relationList = new ArrayList<>();
        relationList.add(new PositionCondition("1", "2", 2));
        relationList.add(new PositionCondition("1", "3", 2));
        relationList.add(new PositionCondition("2", "6", 2));
        relationList.add(new PositionCondition("3", "6", 2));
        relationList.add(new PositionCondition("3", "5", 2));
        relationList.add(new PositionCondition("4", "5", 12));
        relationList.add(new PositionCondition("1", "4", 2));
        relationList.add(new PositionCondition("4", "6", 2));
        relationList.add(new PositionCondition("5", "4", 2));
        relationList.add(new PositionCondition("3", "5", 2));
        relationList.add(new PositionCondition("4", "3", 2));
        relationList.add(new PositionCondition("3", "6", 2));

        JobInfo jobInfo = new JobInfo("1", "6");

        List<PositionCondition> path = findShorPath(jobInfo, relationList);

        for (PositionCondition station : path) {
            System.out.println(station);
        }

    }

    public static List<PositionCondition> findShorPath(JobInfo jobInfo, List<PositionCondition> positionConditionList) {

        //获取路径的起始点
        String start = jobInfo.getFromCellCode();
        String end = jobInfo.getToCellCode();

        // 初始化距离和父节点映射
        Map<String, Integer> distances = new HashMap<>();
        //previous代表走过的路径
        Map<String, String> previous = new HashMap<>();

        for (PositionCondition relation : positionConditionList) {
            //初始化距离：[{起点1，无穷远},{起点2，无穷远}]
            if (!distances.containsKey(relation.getFromCode())) {
                distances.put(relation.getFromCode(), Integer.MAX_VALUE);
            }
            //初始化距离：[{终点1，无穷远},{终点1，无穷远}]
            if (!distances.containsKey(relation.getToCode())) {
                distances.put(relation.getToCode(), Integer.MAX_VALUE);
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
            for (PositionCondition relation : positionConditionList) {
                if (relation.getFromCode().equals(current)) {
                    //当前点可能到达的下一个点的信息,以及从起始点到该点位的距离
                    String neighbor = relation.getToCode();
                    int newDistance = distances.get(current) + relation.getTaskTime();

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

        if (path.size() < 2) {
            throw new RuntimeException("未找到路径");
        }
        List<PositionCondition> list = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            List<PositionCondition> nextList = positionConditionList.stream().filter(s -> from.equals(s.getFromCode()) && to.equals(s.getToCode())).collect(Collectors.toList());
            list.add(nextList.get(0));
        }

        return list;
    }
}
