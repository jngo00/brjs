package org.bladerunnerjs.core.plugin.command;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;


public interface CommandPlugin extends Plugin
{
	public String getCommandName();
	public String getCommandDescription();
	public String getCommandUsage();
	public String getCommandHelp();
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException;
}
