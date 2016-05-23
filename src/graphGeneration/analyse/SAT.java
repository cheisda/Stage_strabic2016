package graphGeneration.analyse;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

// voir http://www.sat4j.org/howto.php#source to use this
public class SAT {
	public void SAT(String[] args) {
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(3600); // 1 hour timeout
		DimacsReader reader = new DimacsReader(solver);
		// CNF filename is given on the command line 
		try {
			IProblem problem = reader.parseInstance(args[0]);
			if (problem.isSatisfiable()) {
				System.out.println("Satisfiable !");
				System.out.println(reader.decode(problem.model()));
			} else {
				System.out.println("Unsatisfiable !");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (ParseFormatException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (ContradictionException e) {
			System.out.println("Unsatisfiable (trivial)!");
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");      
		}
	}
}

