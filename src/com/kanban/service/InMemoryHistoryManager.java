package com.kanban.service;

import com.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;
    private int size = 0;

    private static class Node {
        Node next;
        Node prev;
        Task task;

        public Node(Node prev, Task task, Node next) {
            this.next = next;
            this.task = task;
            this.prev = prev;
        }
    }

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
            history.remove(task.getId());
        }
        linkLast(task);
        history.put(task.getId(), tail);
    }

    public void remove(int id) {
        if (history.containsKey(id)) {
            if (head == null) {
                return;
            }

            if (head == tail) {
                head = null;
                tail = null;
                size--;
                return;
            }

            Node node = history.get(id);
            if (node == head) {
                head.next.prev = null;
                head = head.next;
                size--;
                return;
            }
            if (node == tail) {
                tail.prev.next = null;
                tail = tail.prev;
                size--;
                return;
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
            size--;
        }
    }

    public int getSize() {
        return size;
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
}