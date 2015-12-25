package invertPackage;

import java.io.PrintWriter;

public class SortHash implements Comparable<SortHash> {

	private int docId;
	private double simVal;
	
	public SortHash( int docId, double simVal ) {
		this.docId = docId;
		this.simVal = simVal;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public double getSimVal() {
		return simVal;
	}

	public void setSimVal(double simVal) {
		this.simVal = simVal;
	}

	@Override
	public int compareTo(SortHash other) {
		double result = other.getSimVal() - this.getSimVal();
		return result > 0 ? 1 : (result == 0 ? 0 : -1);
	}

	@Override
	public String toString() {
		PrintWriter out = null;
		
		return "[docId: " + docId + ", similarityVal: " + simVal + "]";
	}
}
