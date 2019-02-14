package jarhead;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * Connection Gene class for node connections.
 */
public class ConnectionGene implements Serializable {
	private static final long serialVersionUID = 139348938L;

	private int inNode;
	private int outNode;
	private float weight;
	private boolean expressed;
	private int innovation;

	/**
	 * Constructs a new connection gene.
	 * 
	 * @param inNode     input node
	 * @param outNode    output node
	 * @param weight     weight of the node
	 * @param expressed  boolean whether node is expressed or disabled
	 * @param innovation global innovation number
	 */
	public ConnectionGene(int inNode, int outNode, float weight, boolean expressed, int innovation) {
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
		this.expressed = expressed;
		this.innovation = innovation;
	}

	/**
	 * Construct a new connection gene without innovation
	 * 
	 * @param inNode
	 * @param outNode
	 * @param weight
	 * @param expressed
	 */
	public ConnectionGene(int inNode, int outNode, float weight, boolean expressed) {
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
		this.expressed = expressed;
		// TODO: default innovation value
	}

	/**
	 * Inherits a connection gene.
	 * 
	 * @param con Connection gene to be copied
	 */
	public ConnectionGene(ConnectionGene con) {
		this.inNode = con.inNode;
		this.outNode = con.outNode;
		this.weight = con.weight;
		this.expressed = con.expressed;
		this.innovation = con.innovation; // innovation is same as map key due to iterative nature of both algorithms
	}

	// utility functions
	public int getInNode() {
		return inNode;
	}

	public int getOutNode() {
		return outNode;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float newWeight) {
		this.weight = newWeight;
	}

	public boolean isExpressed() {
		return expressed;
	}

	public void enable() {
		expressed = true;
	}

	public void disable() {
		expressed = false;
	}

	public int getInnovation() { // bad name. used as counter iterator (getter/setter)
		return innovation;
	}

	/**
	 * @return new connectionGene with identical inNode outNode weight expression
	 *         and innovation number.
	 */
	public ConnectionGene copy() {
		return new ConnectionGene(inNode, outNode, weight, expressed, innovation);
	}

	/**
	 * Checks this connection against all connections in a Gene pool. Used for
	 * global consistency of Connection innovation.
	 * 
	 * @param genomes              List of all genomes to be compared against.
	 * @param connectionInnovation Counter for innovation number
	 * @return True if innovation is matched and assigned
	 */

	public boolean globalCheck(List<Genome> genomes, Counter connectionInnovation) {
		// TODO: move this method into constructor or add back to Genome
		// addConnectionMutation method (proper oop solution). prefer to move as much
		// innovation related code into this method (genome is bloated and becoming
		// imperative)

		Optional<ConnectionGene> match = genomes.parallelStream().map(g -> g.getConnectionGenes())
				.flatMap(c -> c.values().parallelStream().filter(l -> l.inNode == inNode && l.outNode == outNode))
				.findAny();

		// need to add condition for recurrent connections, cyclic will appear as normal
		// connections wrt this method.

		if (match.isPresent()) {
			this.innovation = match.get().innovation; // TODO: reset innovation etc. within this method. WIP
			return true;
		} else { // novel connection
			this.innovation = connectionInnovation.updateInnovation();
//			System.out.println("Novel connection: " + innovation);
			return false;
		}
	}
}