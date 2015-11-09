package server.facade;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import client.facade.ClientFacade;
import server.manager.ServerManager;
import shared.utils.Interpreter;
import shared.communication.moves.*;
import shared.definitions.ResourceType;
import shared.locations.*;
import shared.models.ClientModel;
import shared.models.Game;
import shared.models.Player;
import shared.models.ResourceCards;
import shared.models.TradeOffer;

public class MovesFacade_Test 
{
	private Interpreter interpreter = new Interpreter();
	private IMovesFacade mf;
	private int gameID;
	ClientModel ogModel;
	
	@Before
	public void setUp() throws Exception
	{
		StringBuilder result = new StringBuilder("");
	    File file = new File("MovesFacadeTestJSON.txt");
		
		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String jsonString = result.toString();
				
		JsonElement jsonToParse = new JsonParser().parse(jsonString).getAsJsonObject();

		ogModel = interpreter.deserialize(jsonToParse);
		
		ServerManager.instance().setFacades();
		
		gameID = ServerManager.instance().getGamesManager().addNewGameGetID(ogModel, "TITLE");
		mf = ServerManager.instance().getMovesFacade();
				
	}

	@Test
	public void sendChat_Test()
	{
		SendChat_Input params1 = new SendChat_Input(1, ""); //empty string will fail
		SendChat_Input params2 = new SendChat_Input(1, null); //player not in game
		SendChat_Input params3 = new SendChat_Input(-1, ""); //incorrect playerID
		
		SendChat_Input params4 = new SendChat_Input(1, "Let's mess up the gameID");
		SendChat_Input params5 = new SendChat_Input(1, "Let's mess up the userID");
		
		SendChat_Input params6 = new SendChat_Input(1, "This better work");
		
		
		assertNull(mf.sendChat(params1,1,gameID));
		assertNull(mf.sendChat(params2,1,gameID));
		assertNull(mf.sendChat(params3,1,gameID));
		
		assertNull(mf.sendChat(params4,1,-4206669));
		assertNull(mf.sendChat(params5,-4206669,gameID));
		
		ClientModel newModel = interpreter.deserialize(mf.sendChat(params6,1,gameID)); //send the chat
		assertTrue(newModel.getChat().getLines().length == 1); //message was added
		assertTrue(newModel.getChat().getLines()[0].getMessage().equals("This better work")); //message was correct
		assertTrue(newModel.getChat().getLines()[0].getSource().equals("Brooke")); //message source was correct
	}
	
	
	@Test
	public void rollNumber_Test()
	{
		RollNumber_Input params1 = new RollNumber_Input(1, 1); //wrong turn
		RollNumber_Input params2 = new RollNumber_Input(0, 13); //wrong number
		RollNumber_Input params3 = new RollNumber_Input(-4, 9); //wrong playerID
		RollNumber_Input params4 = new RollNumber_Input(0, 12);
		RollNumber_Input params5 = new RollNumber_Input(0, 2);
		
		RollNumber_Input params6 = new RollNumber_Input(0, 12);//good
		
		RollNumber_Input params7 = new RollNumber_Input(0, 7); //good
		
		
		assertNull(mf.rollNumber(params1,1,gameID));
		assertNull(mf.rollNumber(params2,1,gameID));
		assertNull(mf.rollNumber(params3,1,gameID));
		assertNull(mf.rollNumber(params4,1,-4206669));
		assertNull(mf.rollNumber(params5,-4206669,gameID));
		
		ClientModel newModel = interpreter.deserialize(mf.rollNumber(params6,1,gameID)); //roll baby roll
		//todo
	}
//	
//	
//	@Test
//	public void canRobPlayer_Test()
//	{
//		RobPlayer_Input params1 = new RobPlayer_Input(0, 0, new HexLocation(0,1)); //same person
//		RobPlayer_Input params3 = new RobPlayer_Input(1, 0, new HexLocation(0,1)); //not player's turn
//		RobPlayer_Input params4 = new RobPlayer_Input(0, 3, new HexLocation(2,-2)); //robber is there
//		RobPlayer_Input params5 = new RobPlayer_Input(0, 2, new HexLocation(-4,7)); //invalid hex
//		
//		RobPlayer_Input params6 = new RobPlayer_Input(0, 3, new HexLocation(-1,1)); //g2g
//		
//		
//		assertFalse(mf.canRobPlayer(params1));
//		assertFalse(mf.canRobPlayer(params3));
//		assertFalse(mf.canRobPlayer(params4));
//		assertFalse(mf.canRobPlayer(params5));
//		
//		assertTrue(mf.canRobPlayer(params6));
//	}
//	
//	@Test
//	public void canFinishTurn_Test()
//	{
//		FinishTurn_Input params1 = new FinishTurn_Input(1); //not their turn
//		
//		FinishTurn_Input params2 = new FinishTurn_Input(0); //g2g
//		
//		
//		assertFalse(mf.canFinishTurn(params1));
//		
//		assertFalse(mf.canFinishTurn(params2)); //not correct part of turn
//		
//		clientModel.getTurnTracker().setStatus("playing");
//		
//		assertTrue(mf.canFinishTurn(params2));
//		
//		clientModel.getTurnTracker().setStatus("rolling");
//	}
//	
//	@Test
//	public void canBuyDevCard_Test()
//	{
//		BuyDevCard_Input params1 = new BuyDevCard_Input(1); //not their turn
//		assertFalse(mf.canBuyDevCard(params1));
//		
//		BuyDevCard_Input params2 = new BuyDevCard_Input(0); //nice!
//		assertTrue(mf.canBuyDevCard(params2));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(3); //switch turns
//		
//		BuyDevCard_Input params3 = new BuyDevCard_Input(3);  //not enough cards
//		assertFalse(mf.canBuyDevCard(params3));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canYearOfPlenty_Test()
//	{
//		YearOfPlenty_Input params1 = new YearOfPlenty_Input(0, ResourceType.BRICK, ResourceType.SHEEP); //good
//		assertTrue(mf.canYearOfPlenty(params1));
//		
//		YearOfPlenty_Input params2 = new YearOfPlenty_Input(1, ResourceType.BRICK, ResourceType.SHEEP); //wrong turn
//		assertFalse(mf.canYearOfPlenty(params2));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turns
//		
//		YearOfPlenty_Input params3 = new YearOfPlenty_Input(1, ResourceType.BRICK, ResourceType.SHEEP); //no old cards to play
//		assertFalse(mf.canYearOfPlenty(params3));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(2); //switch turns
//		
//		YearOfPlenty_Input params4 = new YearOfPlenty_Input(2, ResourceType.BRICK, ResourceType.SHEEP); //already played this turn
//		assertFalse(mf.canYearOfPlenty(params4));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(3); //switch turns
//		
//		YearOfPlenty_Input params5 = new YearOfPlenty_Input(3, ResourceType.BRICK, ResourceType.SHEEP); //doesnt have card
//		assertFalse(mf.canYearOfPlenty(params5));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canRoadBuilding_Test()
//	{
//		Player p = clientModel.getPlayerByIndex(0);
//		EdgeLocation e1 = new EdgeLocation(new HexLocation(0,0), EdgeDirection.North); //free
//		EdgeLocation e2 = new EdgeLocation(new HexLocation(0,0), EdgeDirection.NorthWest); //free
//		EdgeLocation e3 = new EdgeLocation(new HexLocation(0,2), EdgeDirection.North); //taken
//		EdgeLocation e4 = new EdgeLocation(new HexLocation(0,3), EdgeDirection.NorthEast); //off map
//		
//		p.setRoads(1);
//		
//		RoadBuilding_Input params2 = new RoadBuilding_Input(0, e1, null); //no
//		RoadBuilding_Input params3 = new RoadBuilding_Input(0, e1, e2); //no
//		RoadBuilding_Input params4 = new RoadBuilding_Input(0, e3, null); //no
//		RoadBuilding_Input params5 = new RoadBuilding_Input(0, null, e3); //no
//		assertFalse(mf.canRoadBuilding(params2));
//		assertFalse(mf.canRoadBuilding(params3));
//		assertFalse(mf.canRoadBuilding(params4));
//		assertFalse(mf.canRoadBuilding(params5));
//		
//		p.setRoads(13);
//		
//		RoadBuilding_Input params6 = new RoadBuilding_Input(0, e1, e2); //yes
//		RoadBuilding_Input params7 = new RoadBuilding_Input(2, e1, e2); //false
//		RoadBuilding_Input params8 = new RoadBuilding_Input(0, e1, e4); //false
//		assertTrue(mf.canRoadBuilding(params6));
//		assertFalse(mf.canRoadBuilding(params7));
//		assertFalse(mf.canRoadBuilding(params8));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn
//		
//		RoadBuilding_Input params9 = new RoadBuilding_Input(1, e1, e2); //no
//		assertFalse(mf.canRoadBuilding(params9));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn bacc
//	}
//	
//	@Test
//	public void canSoldier_Test()
//	{
//		Soldier_Input params1 = new Soldier_Input(0, 3, new HexLocation(2, -2)); //robber already here
//		Soldier_Input params2 = new Soldier_Input(0, 2, new HexLocation(1, -1)); //good
//		Soldier_Input params3 = new Soldier_Input(0, 0, new HexLocation(1, -1)); //cant rob self
//		Soldier_Input params4 = new Soldier_Input(0, 3, new HexLocation(0, 0)); //not at location
//		Soldier_Input params5 = new Soldier_Input(0, 3, new HexLocation(-3, 1)); //not a land location
//		
//		assertFalse(mf.canSoldier(params1));
//		assertTrue(mf.canSoldier(params2));
//		assertFalse(mf.canSoldier(params3));
//		assertFalse(mf.canSoldier(params4));
//		assertFalse(mf.canSoldier(params5));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn
//		
//		Soldier_Input params6 = new Soldier_Input(1, 2, new HexLocation(1, -1)); //no dev card
//		assertFalse(mf.canSoldier(params6));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canMonument_Test()
//	{
//		Monument_Input params1 = new Monument_Input(0); //bad
//		assertFalse(mf.canMonument(params1));
//		
//		Monument_Input params2 = new Monument_Input(1); //wrong turn
//		assertFalse(mf.canMonument(params2));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turns
//		
//		Monument_Input params3 = new Monument_Input(1); //monument is a new card
//		assertFalse(mf.canMonument(params3)); //failing
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(2); //switch turns
//		
//		Monument_Input params4 = new Monument_Input(2); //already played this turn
//		assertFalse(mf.canMonument(params4));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(3); //switch turns
//		
//		Monument_Input params5 = new Monument_Input(3); //doesnt have card
//		assertFalse(mf.canMonument(params5));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canBuildRoad_Test()
//	{
//		BuildRoad_Input params1 = new BuildRoad_Input(0, new EdgeLocation(new HexLocation(2,1), EdgeDirection.North), false); //good
//		BuildRoad_Input params2 = new BuildRoad_Input(0, new EdgeLocation(new HexLocation(2,1), EdgeDirection.NorthEast), false); //not connected
//		BuildRoad_Input params3 = new BuildRoad_Input(0, new EdgeLocation(new HexLocation(1,1), EdgeDirection.NorthEast), false); //location already taken
//		assertTrue(mf.canBuildRoad(params1));
//		assertFalse(mf.canBuildRoad(params2));
//		assertFalse(mf.canBuildRoad(params3));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn
//		
//		BuildRoad_Input params4 = new BuildRoad_Input(1, new EdgeLocation(new HexLocation(2,1), EdgeDirection.North), true); //good
//		BuildRoad_Input params5 = new BuildRoad_Input(1, new EdgeLocation(new HexLocation(-3,2), EdgeDirection.North), false); //not on land
//		BuildRoad_Input params6 = new BuildRoad_Input(1, new EdgeLocation(new HexLocation(1,1), EdgeDirection.NorthEast), true); //location already taken
//		assertTrue(mf.canBuildRoad(params4));
//		assertFalse(mf.canBuildRoad(params5));
//		assertFalse(mf.canBuildRoad(params6));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canBuildSettlement_Test()
//	{
//		Player p = clientModel.getPlayerByIndex(0);
//		Player p3 = clientModel.getPlayerByIndex(3);
//		clientModel.getMap().getRoads().put(new EdgeLocation(new HexLocation(2,0),EdgeDirection.NorthWest), p);
//		clientModel.getMap().getRoads().put(new EdgeLocation(new HexLocation(1,1),EdgeDirection.North), p);
//		clientModel.getMap().getRoads().put(new EdgeLocation(new HexLocation(-1,1),EdgeDirection.NorthWest), p3);
//		
//		
//		BuildSettlement_Input params1 = new BuildSettlement_Input(0, new VertexLocation(new HexLocation(2,0), VertexDirection.NorthWest),false); //true
//		BuildSettlement_Input params2 = new BuildSettlement_Input(0, new VertexLocation(new HexLocation(1,1), VertexDirection.NorthWest),false); //too close
//		BuildSettlement_Input params3 = new BuildSettlement_Input(0, new VertexLocation(new HexLocation(-1,0), VertexDirection.NorthEast),false); //nooo
//		BuildSettlement_Input params4 = new BuildSettlement_Input(0, new VertexLocation(new HexLocation(2,1), VertexDirection.NorthWest),false); //something already there
//		BuildSettlement_Input params5 = new BuildSettlement_Input(1, new VertexLocation(new HexLocation(2,1), VertexDirection.NorthWest),false); //wrong turn
//		assertTrue(mf.canBuildSettlement(params1));
//		assertFalse(mf.canBuildSettlement(params2));
//		assertFalse(mf.canBuildSettlement(params3));
//		assertFalse(mf.canBuildSettlement(params4));
//		assertFalse(mf.canBuildSettlement(params5));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(3); //switch turn back
//		
//		BuildSettlement_Input params6 = new BuildSettlement_Input(3, new VertexLocation(new HexLocation(-1,1), VertexDirection.NorthWest),false); //no supplies
//		assertFalse(mf.canBuildSettlement(params6));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//		
//		clientModel.getMap().getRoads().remove(new EdgeLocation(new HexLocation(2,0),EdgeDirection.NorthWest));
//		clientModel.getMap().getRoads().remove(new EdgeLocation(new HexLocation(1,1),EdgeDirection.North));
//		clientModel.getMap().getRoads().remove(new EdgeLocation(new HexLocation(-1,1),EdgeDirection.NorthWest));
//	}
//	
//	@Test
//	public void canBuildCity_Test()
//	{
//		BuildCity_Input params1 = new BuildCity_Input(0, new VertexLocation(new HexLocation(2,1), VertexDirection.NorthWest)); //yee
//		BuildCity_Input params2 = new BuildCity_Input(0, new VertexLocation(new HexLocation(0,1), VertexDirection.NorthWest)); //no settlement here
//		assertTrue(mf.canBuildCity(params1));
//		assertFalse(mf.canBuildCity(params2));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn back
//		
//		BuildCity_Input params3 = new BuildCity_Input(1, new VertexLocation(new HexLocation(0,1), VertexDirection.NorthWest)); //no resources
//		assertFalse(mf.canBuildCity(params3));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canOfferTrade_Test()
//	{
//		ResourceCards r = new ResourceCards(-1,-2,-1,-1,-3);
//		
//		OfferTrade_Input params1 = new OfferTrade_Input(0, r, 3); //yes
//		OfferTrade_Input params2 = new OfferTrade_Input(0, r, 0); //no
//		assertTrue(mf.canOfferTrade(params1));
//		assertFalse(mf.canOfferTrade(params2));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn
//		
//		OfferTrade_Input params3 = new OfferTrade_Input(1, r, 0); //no cards
//		assertTrue(mf.canOfferTrade(params3));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canAccepTrade_Test()
//	{
//		ResourceCards r = new ResourceCards(1,2,1,1,3);
//		clientModel.setTradeOffer(new TradeOffer());
//		clientModel.getTradeOffer().setOffer(r);
//		clientModel.getTradeOffer().setReceiver(0);
//		clientModel.getTradeOffer().setSender(1);
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn
//		
//		AcceptTrade_Input params1 = new AcceptTrade_Input(0, false); //good
//		AcceptTrade_Input params2 = new AcceptTrade_Input(0, true); //good
//		AcceptTrade_Input params3 = new AcceptTrade_Input(1, false); //wrong user
//		assertTrue(mf.canAcceptTrade(params1));
//		assertTrue(mf.canAcceptTrade(params2));
//		assertFalse(mf.canAcceptTrade(params3));
//		
//		clientModel.getTradeOffer().setReceiver(2);
//		AcceptTrade_Input params4 = new AcceptTrade_Input(2, true);
//		assertTrue(mf.canAcceptTrade(params4));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canMaritimeTrade_Test()
//	{
//		MaritimeTrade_Input params1 = new MaritimeTrade_Input(0, 3, ResourceType.ORE, ResourceType.WHEAT); //good
//		MaritimeTrade_Input params2 = new MaritimeTrade_Input(0, 2, ResourceType.ORE, ResourceType.WHEAT); //not a 2 port
//		MaritimeTrade_Input params3 = new MaritimeTrade_Input(0, 3, ResourceType.WOOD, ResourceType.WHEAT); //not enough resources
//		MaritimeTrade_Input params4 = new MaritimeTrade_Input(0, 4, ResourceType.ORE, ResourceType.WHEAT); //yassss
//		MaritimeTrade_Input params5 = new MaritimeTrade_Input(1, 4, ResourceType.ORE, ResourceType.WHEAT); //nooooo
//		
//		assertTrue(mf.canMaritimeTrade(params1));
//		assertFalse(mf.canMaritimeTrade(params2));
//		assertFalse(mf.canMaritimeTrade(params3));
//		assertTrue(mf.canMaritimeTrade(params4));
//		assertFalse(mf.canMaritimeTrade(params5));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(1); //switch turn
//		
//		MaritimeTrade_Input params6 = new MaritimeTrade_Input(1, 2, ResourceType.WOOD, ResourceType.WHEAT); //yesss
//		MaritimeTrade_Input params7 = new MaritimeTrade_Input(1, 3, ResourceType.WOOD, ResourceType.WHEAT); //nooooo
//		MaritimeTrade_Input params8 = new MaritimeTrade_Input(1, 2, ResourceType.ORE, ResourceType.WHEAT); //nooooo
//		
//		assertTrue(mf.canMaritimeTrade(params6));
//		assertFalse(mf.canMaritimeTrade(params7));
//		assertFalse(mf.canMaritimeTrade(params8));
//		
//		cf.getClientModel().getTurnTracker().setCurrentTurn(0); //switch turn back
//	}
//	
//	@Test
//	public void canDiscardCards_Test()
//	{
//		ResourceCards r1 = new ResourceCards(1,2,1,1,3);
//		ResourceCards r2 = new ResourceCards(3,2,1,1,3);
//		
//		DiscardCards_Input params1 = new DiscardCards_Input(0, r1); //yes
//		DiscardCards_Input params2 = new DiscardCards_Input(0, r2); //too many cards
//		DiscardCards_Input params3 = new DiscardCards_Input(1, r2); //wrong turn
//		
//		assertTrue(mf.canDiscardCards(params1));
//		assertFalse(mf.canDiscardCards(params2));
//		assertFalse(mf.canDiscardCards(params3));
//		
//		clientModel.getPlayerByIndex(0).setDiscarded(true);
//		
//		DiscardCards_Input params4 = new DiscardCards_Input(0, r1); //already discarded
//		assertFalse(mf.canDiscardCards(params4));
//	}

}
