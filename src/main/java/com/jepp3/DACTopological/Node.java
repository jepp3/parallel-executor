package com.jepp3.DACTopological;

import java.util.concurrent.Callable;

public class Node implements Callable<String>, Comparable<Node> {

    private String name;
    private boolean visited;
    private Task task;

    Node(String name, Task task) {
	this.name = name;
	this.visited = false;
	this.task = task;
    }

    public boolean isVisited() {
	return this.visited;
    }

    public Task getTask() {
	return this.task;
    }

    /**
     * Will mark the node as visited by the TaskRunner
     */
    public void visit() {
	this.visited = true;
    }

    /**
     * This will make the node as unvisited
     */
    public void unVisit() {
	this.visited = false;
    }

    /**
     * The executor will call this method and call the taskToPerform method
     */
    @Override
    public String call() throws Exception {
	String val = task.execute("");
	return val;
    }

    /**
     * Override this method for better comparisons
     */
    @Override
    public int compareTo(Node compare) {
	return (this.name).compareTo(compare.name);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
