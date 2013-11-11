package org.bladerunnerjs.model.utility;


public class EmptyTrieKeyException extends Exception
{
	private static final long serialVersionUID = -8329081207560424876L;

	public EmptyTrieKeyException()
	{
		super("Trie keys cannot be empty");
	}
	
}
