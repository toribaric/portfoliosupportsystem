package org.nnga.tsp.persistence.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "NeuronLayer")
public class NeuronLayer implements PersistenceEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "NumNeurons")
    private int numNeurons;

    @ManyToOne
    @JoinColumn(name = "NeuralNetworkId")
    private NeuralNetwork neuralNetwork;

    @OneToMany(mappedBy = "neuronLayer", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Neuron> neurons;

    public NeuronLayer() {

    }

    public NeuronLayer(int numNeurons, List<Neuron> neurons) {
        this.numNeurons = numNeurons;
        for( Neuron neuron : neurons ) {
            neuron.setNeuronLayer(this);
        }
        this.neurons = neurons;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumNeurons() {
        return numNeurons;
    }

    public void setNumNeurons(int numNeurons) {
        this.numNeurons = numNeurons;
    }

    @JsonIgnore
    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        for( Neuron neuron : neurons ) {
            neuron.setNeuronLayer(this);
        }
        this.neurons = neurons;
    }

}
