package com.kanban.service;

import com.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyIndex = new HashMap<>();
    private Node head;
    private Node tail;

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(task);
        newNode.prev = oldTail;
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    @Override
    public void add(Task task) {
        if (historyIndex.containsKey(task.getId())) {
            remove(task.getId());
            historyIndex.remove(task.getId());
        }
        linkLast(task);
        historyIndex.put(task.getId(), tail);
    }

    public void remove(int id) {
        if (!historyIndex.containsKey(id)) {
            return;
        }

        if (head == null) {
            return;
        }

        if (head == tail) {
            head = null;
            tail = null;
            return;
        }

        Node node = historyIndex.get(id);
        if (node == head) {
            head.next.prev = null;
            head = head.next;
            return;
        }
        if (node == tail) {
            tail.prev.next = null;
            tail = tail.prev;
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }

    private List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            historyList.add(node.task);
            node = node.next;
        }
        return historyList;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node {
        Node next;
        Node prev;
        Task task;

        public Node(Task task) {
            this.task = task;
        }
    }
}