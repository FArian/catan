package server.command;

import com.google.gson.JsonElement;
import com.sun.net.httpserver.HttpExchange;

public class ExchangeWrapper 
{
	private HttpExchange exchange;
	private JsonElement json;
	
	/**
	 * Wrapper class that holds http exchange. If no http exchange is provided,
	 * a default json can be added to simulate the json provided by the http
	 * exchange object. To do this, simply pass null to the constructor.
	 * @param exchange
	 */
	public ExchangeWrapper(HttpExchange exchange)
	{
		this.setExchange(exchange);
	}

	public HttpExchange getExchange() {
		return exchange;
	}

	public void setExchange(HttpExchange exchange) {
		this.exchange = exchange;
	}
	
	public JsonElement getJson()
	{
		assert(exchange == null);
		return this.json;
	}
	
	public void setJson(JsonElement json)
	{
		this.json = json;
	}
}