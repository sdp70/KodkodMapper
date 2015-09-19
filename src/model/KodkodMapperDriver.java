package model;

import java.util.Iterator;

import kodkod.ast.Formula;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;

public class KodkodMapperDriver {
	
	
	/* Main Method. */
	public static void main(String[] args)
	{
		/*
		 * Build a queue of linked-list.
		 */
		LinkedQueue<Integer> lQueue = new LinkedQueue<Integer>();
		
		/*
		 * Add nodes to the linked-list queue.
		 */
		lQueue.put(10);
		lQueue.put(20);
		lQueue.put(30);	
		
		//TEST CODE : SATISFIABLE INSTANCE
		lQueue.getHead().next = null;

		/*
		 * Print the test linked-list.
		 */
		System.out.println("\nCurrent linked-queue: ");
		
		Iterator<LinkedQueue.Node<Integer>> iterNodes = lQueue.getNodes();		
		while(iterNodes.hasNext()){
			
			LinkedQueue.Node<Integer> node = iterNodes.next();
			System.out.println(node.getName());
		}
		
		/*
		 * Test reachability of linked-list.
		 * 
		 * Every node should be reachable from head by "next" relationship.
		 */
		
		//Instantiate the KodkodMaper class...
		KodkodMapper<Integer> kMapper = new KodkodMapper<Integer>(lQueue);
		

		//Create a solver instance
		Solver solver = new Solver();
		solver.options().setSolver(SATFactory.DefaultSAT4J);
	
		
		//Build kodkod queue...
		Bounds bQueue = kMapper.buildKodkodQueue();
		
		//Get the formula to check reachability...	
		final Formula f = kMapper.getReachabilityFormula();			
		
		//Inverse the formula and solve on bounded Kodkod queue...
		Solution sol = solver.solve(f , bQueue);
		
		//Print the solution..
		System.out.println("\n"+sol);
	}

	//TODO:
	/* 
	 * 
	 * Try to create test driver that gradually increases the number of elements put into it.
	 * - check wait time
	 * 
	 * - synchronize traveral and checking (Kodkod solver...).
	 * - add 10 elements, add 10 elements next and check traversal/solving only on last 10 elements : incremental checking
	 * 
	 * 1. Look for JPF API that suspends the non-deterministic execution. (BFS traversal) - Google it up.
	 * 
	 * 2. Look up some classes or APIs in JPF such as Verify.
	 * 
	 * 3. Find out how to start runJPF from inside your own application.
	 * - normal java program (separate java project.)
	 * - invoke runJPf class from that java program.
	 * -check online.
	 * -check RunJPF class : API => input as .jpf file.
	 * 
	 * 
	 * - Create a repository send a link for this project.
	 * 
	 * NEXT STEPS:
	 * 
	 * 1. Write a listener for JPF traversal that invoke Property checking.
	 * 
	 * 
	 */
}
