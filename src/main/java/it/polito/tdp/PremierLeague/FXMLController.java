package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.BestPlayer;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.simulation.SimulatorResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController 
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnGiocatoreMigliore;

    @FXML
    private Button btnSimula;

    @FXML
    private ComboBox<Match> cmbMatch;

    @FXML
    private TextField txtN;

    @FXML
    private TextArea txtResult;
    
    private Model model;
    
    

    @FXML
    void doCreaGrafo(ActionEvent event) 
    {
    	Match selectedMatch = this.cmbMatch.getValue();
    	
    	if(selectedMatch == null)
    	{
    		this.txtResult.setText("Errore: seleziona un match");
    		return;
    	}
    	
    	this.model.createGraph(selectedMatch);
    	
    	int numVertices = this.model.getNumVertices();
    	int numEdges = this.model.getNumEdges();
    	
    	this.txtResult.setText(String.format("GRAFO CREATO\n# Vertici: %d\n# Archi: %d",
    			numVertices, numEdges));
    }

    @FXML
    void doGiocatoreMigliore(ActionEvent event) 
    {
    	BestPlayer bestPlayer = this.model.getBestPlayer();
    	
    	if(bestPlayer == null)
    	{
    		this.txtResult.setText("Errore: devi prima creare il grafo");
    		return;
    	}
    	
    	this.txtResult.setText(String.format("GIOCATORE MIGLIORE: %s\nDELTA EFFICIENZA: %s",
    			bestPlayer.getBestPlayer().toString(), bestPlayer.getScore()));
    }

    @FXML
    void doSimula(ActionEvent event) 
    {
    	String input = this.txtN.getText();
    	
    	if(input == null || input.isBlank())
    	{
    		this.txtResult.setText("Errore: inserire numero di azioni minime N");
    		return;
    	}
    	
    	int N;
    	
    	try
		{
			N = Integer.parseInt(input);
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.setText("Errore di formato: inserire un numero intero N ("+input+" non è un numero intero)");
    		return;
		}
    	
    	SimulatorResult result = this.model.runNewSimulation(N);
    	
    	if(result == null)
    	{
    		this.txtResult.setText("Errore: calcolare prima il giocatore migliore");
    		return;
    	}
    	
    	this.txtResult.setText(String.format(
    			"RISULTATO PARTITA:\n%s - %s  => %d - %d\nNUMERO ESPULSIONI: %d - %d",
    			result.getTeamHome().toString(), result.getTeamAway(), result.getGoalsTeamHome(),
    			result.getGoalsTeamAway(), result.getRedCardsTeamHome(), result.getRedCardsTeamAway()));
    }

    @FXML
    void initialize() 
    {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene_PremierLeague.fxml'.";
        assert btnGiocatoreMigliore != null : "fx:id=\"btnGiocatoreMigliore\" was not injected: check your FXML file 'Scene_PremierLeague.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene_PremierLeague.fxml'.";
        assert cmbMatch != null : "fx:id=\"cmbMatch\" was not injected: check your FXML file 'Scene_PremierLeague.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene_PremierLeague.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene_PremierLeague.fxml'.";
    }
    
    public void setModel(Model model)
    {
    	this.model = model;
    	
    	Collection<Match> allMatches = this.model.getAllMatches();
    	this.cmbMatch.getItems().addAll(allMatches);
    }
}
