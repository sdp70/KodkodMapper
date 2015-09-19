package model;

import kodkod.instance.*;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.engine.*;
import kodkod.engine.satlab.SATFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class KodkodMapper<E> {

private LinkedQueue<E> queue;
private final Relation Node, next, head, tail;

public KodkodMapper(LinkedQueue<E> queue){
	Node = Relation.unary("Node");
	head = Relation.unary("head");
	tail = Relation.unary("tail");
	next = Relation.binary("next");
	
	this.queue = queue;
}

public Formula declarations() {
		/* Node */
	final Formula f0 = next.partialFunction(Node, Node);
	return f0;
}

public final Formula facts() {
	final Variable v = Variable.unary("v");
	//final Variable w = Variable.unary("w");
   
	
	final Formula f0 = v.in(head.join(next.reflexiveClosure()));
 //   final Formula f1 = tail.in(head.join(next.closure())).not();
	final Formula f = f0.forAll(v.oneOf(Node));
	
	return f;
	
}

public final Formula empty() {
	//return declarations();
	return declarations().and(facts());
}

public final Bounds buildKodkodQueue(){
	
	int size = queue.size();
	final List<String> atoms = new ArrayList<String>(size + 1);
	Iterator<LinkedQueue.Node<E>> iterNodes = queue.getNodes();
	
	while(iterNodes.hasNext()){
		
		LinkedQueue.Node<E> node = iterNodes.next();
		atoms.add(node.getName());
	}
	atoms.add("null");
	final Universe u = new Universe(atoms);
	final TupleFactory f = u.factory();
	final Bounds b = new Bounds(u);
	final int max = size - 1;
	System.out.println("Head name = " + queue.getHeadName());
	System.out.println("Tail name = " + queue.getTailName());
	
	b.bound(Node, f.range(f.tuple(queue.getHeadName()), f.tuple( queue.getTailName())));
	b.bound(next, b.upperBound(Node).product(b.upperBound(Node)));
	
	final TupleSet nextTupleSet = f.noneOf(2);
	iterNodes = queue.getNodes();
	final List<LinkedQueue.Node<E>> nodesList = new ArrayList<LinkedQueue.Node<E>>(size);
	while(iterNodes.hasNext()){
		
		LinkedQueue.Node<E> node = iterNodes.next();
		nodesList.add(node);
		System.out.println(node.getName());
	}
	
	
	// bind next relation
	
	for(int i = 0; i < nodesList.size(); i++) {
		if(i == nodesList.size() -1) {
			LinkedQueue.Node<E> node1 = nodesList.get(i);
			
			System.out.println("Tuple "+i+" = ( "+node1.getName() + ", dummy)" );

			nextTupleSet.add(f.tuple(node1.getName(), "dummy"));
		}
		else {
			 // if(i == 3)
			//	continue;
			
			LinkedQueue.Node<E> node1 = nodesList.get(i);
			
			//LinkedQueue.Node<E> node2 = nodesList.get(i+1);
			
			if(node1 != null)
			{
				if(node1.next != null)
				{
					LinkedQueue.Node<E> nextNode = node1.next.get();
					
					if(nextNode != null)
					{
						String nextNodeName = nextNode.getName();
						
						System.out.println("Tuple "+i+" = ( "+node1.getName() + ", " + nextNodeName + ")" );
						
						nextTupleSet.add(f.tuple(node1.getName(), nextNodeName));
						
					}					
				}
				else
				{
					System.out.println(node1.getName() + ".next == null, No tuple generated!");
				}
			}
			
				
			/*
			if(node1 != null) {
				// advance tail
				//tail.compareAndSet(node1, node2);
				System.out.println("Tuple "+i+" = ( "+node1.getName() + ", " + node2.getName() + ")" );
				
				nextTupleSet.add(f.tuple(node1.getName(), node2.getName()));
			}*/
		}
	}
	

	b.boundExactly(next, nextTupleSet);	
	
	// bind head relation
	final TupleSet start = f.noneOf(1);		
	start.add(f.tuple(queue.getHeadName()));										
	b.boundExactly(head , start);										
	
	// bind tail relation
	final TupleSet end = f.noneOf(1);									
	end.add(f.tuple(queue.getTailName()));										
	b.boundExactly(tail , end);	
	
	return b;
	}

	
	/*
	 * Build a formula to check the reachability of a linked-list.
	 */
	public Formula getReachabilityFormula()
	{
		final Variable v = Variable.unary("v");

		final Formula f0 = v.in(head.join(next.reflexiveClosure()));
		final Formula f = f0.forAll(v.oneOf(Node));
		final Formula f1 = f.not();
		
		return f1;
	}
	
}
