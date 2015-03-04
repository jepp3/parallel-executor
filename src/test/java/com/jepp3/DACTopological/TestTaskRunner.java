package com.jepp3.DACTopological;

import static org.junit.Assert.assertEquals;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

public class TestTaskRunner {

    /**
     * Lets make coffe in parallel!!
     * 
     * @throws CycleFoundException
     * @throws InterruptedException
     */
    @Test
    public void testToMakeCoffeInParallel() throws CycleFoundException, InterruptedException {

	DirectedAcyclicGraph<Node, DefaultEdge> dag = new DirectedAcyclicGraph<Node, DefaultEdge>(DefaultEdge.class);

	MockTask task = new MockTask();
	Node drickaKaffe = new Node("Dricka Kaffe", task);
	Node kokaKaffe = new Node("Koka kaffet", task);
	Node hällaIVatten = new Node(" Hälla i vatten", task);
	Node malabönor = new Node(" Mala bönor", task);
	dag.addVertex(drickaKaffe);
	dag.addVertex(kokaKaffe);
	dag.addVertex(hällaIVatten);
	dag.addVertex(malabönor);

	dag.addDagEdge(drickaKaffe, kokaKaffe);
	dag.addDagEdge(kokaKaffe, hällaIVatten);
	dag.addDagEdge(kokaKaffe, malabönor);

	TaskRunner runner = new TaskRunner(dag);

	runner.process();

	// verify that all tasks were executed
	assertEquals(dag.vertexSet().size(), 0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoGraph() throws Exception {
	TaskRunner runner = new TaskRunner(null);
	runner.process();
    }

    @Test
    public void testIndependentTasks() throws CycleFoundException, InterruptedException {

	DirectedAcyclicGraph<Node, DefaultEdge> dag = new DirectedAcyclicGraph<Node, DefaultEdge>(DefaultEdge.class);

	MockTask task = new MockTask();
	Node drickaKaffe = new Node("Dricka Kaffe", task);
	Node kokaKaffe = new Node("Koka kaffet", task);
	Node hällaIVatten = new Node(" Hälla i vatten", task);
	Node malabönor = new Node(" Mala bönor", task);
	dag.addVertex(drickaKaffe);
	dag.addVertex(kokaKaffe);
	dag.addVertex(hällaIVatten);
	dag.addVertex(malabönor);

	TaskRunner runner = new TaskRunner(dag);

	runner.process();

	// verify that all tasks were executed
	assertEquals(dag.vertexSet().size(), 0);

    }

    // used for testing the coffe
    class MockTask implements Task {

	@Override
	public String execute(String inputdata) {
	    final int first = 0;
	    final int last = 2000000000;
	    final int divisor = 3;

	    int amount = 0;
	    for (int i = first; i <= last; i++) {
		if (i % divisor == 0) {
		    amount++;
		}
	    }
	    return Integer.toString(amount);
	}

    }
}
