package com.jepp3.DACTopological;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The taskrunner will run the task defined in a DAG graph.
 * 
 * @author jepster
 *
 */
public class TaskRunner {
    private final static Logger LOGGER = Logger.getLogger(TaskRunner.class.getName());

    final private ExecutorService executor;
    final private CountDownLatch latch;
    private DirectedAcyclicGraph<Node, DefaultEdge> dag;

    public TaskRunner(final DirectedAcyclicGraph<Node, DefaultEdge> directedAcyclicGraph) throws InterruptedException {
	if (directedAcyclicGraph == null) {
	    throw new IllegalArgumentException("dag graph must be defined for the taskRunner");
	}
	dag = directedAcyclicGraph;
	int numberOfNodes = directedAcyclicGraph.vertexSet().size();
	executor = Executors.newFixedThreadPool(numberOfNodes);
	latch = new CountDownLatch(numberOfNodes);

    }

    /**
     * Will start the execution of the tasks based on the DAG graph
     * 
     * @throws InterruptedException
     */
    public void process() throws InterruptedException {

	LOGGER.info("Starting execution of dependency tasks");
	searchForNonDependantNodes(dag);
	latch.await();
	LOGGER.info("Finished execution of tasks");
    }

    private void searchForNonDependantNodes(DirectedAcyclicGraph<Node, DefaultEdge> dag) {

	if (dag.vertexSet().size() == 0) {
	    return;
	}
	synchronized (dag) {
	    Iterator<Node> internalIterator = dag.iterator();
	    Set<Node> executing = new HashSet<Node>();
	    while (internalIterator.hasNext()) {
		Node node = internalIterator.next();
		if (dag.outgoingEdgesOf(node).size() == 0 && !executing.contains(node) && node.isVisited() == false) {
		    executing.add(node);
		}
	    }

	    submitNodesToExecutor(executing);

	}
    }

    /**
     * Will send the set of callables to the executor for execution
     * 
     * @param executing
     */
    private void submitNodesToExecutor(Set<Node> executing) {
	LOGGER.info("Adding tasks to execute to the executor");
	for (Node node : executing) {
	    node.visit();
	    WhenDone<String> futureTask = new WhenDone<String>(node);
	    executor.submit(futureTask);
	}
    }

    /**
     * This future task will re-trigger the search for nodes It will remove the
     * current execute task from the graph
     * 
     * @author jepster
     *
     * @param <V>
     */
    private class WhenDone<V> extends FutureTask<V> {

	private Node currentNode;

	public WhenDone(Callable<V> arg0) {
	    super(arg0);
	    currentNode = (Node) arg0;
	}

	@Override
	protected void done() {
	    goToNextPosibleTask();
	}

	protected void goToNextPosibleTask() {
	    synchronized (dag) {
		latch.countDown(); // important so we know when to stop halting.
		dag.removeVertex(currentNode);
		searchForNonDependantNodes(dag);
	    }
	}

    }

}
