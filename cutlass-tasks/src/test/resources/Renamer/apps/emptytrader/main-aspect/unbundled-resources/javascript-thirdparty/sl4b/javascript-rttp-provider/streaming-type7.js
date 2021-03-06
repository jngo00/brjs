/*
 * Connection using XDomainRequest, based on type 2 to allow for reconnection
 */
 
// used to stop the connection, like type 5 
var g_bStopped = false;

var stop = function()
{
	g_bStopped = true;
	if (this.m_oXDR != null)
	{
		this.m_oXDR.abort();
	}
	this.m_oXDR = null;
	CollectGarbage();
};

/** @private */
var SL4B_StreamingType7 = new function() 
{
	this.m_oXDR = null;
	this.m_nEndPosition = 0;
	this.m_oSL4B_Logger = null;
	this.m_oSL4B_DebugLevel;
	this.m_eParent;
	this.m_bDomainSet = false;
	
	
	this.onunload = function()
	{
		if (this.m_oXDR != null)
		{
			this.m_oXDR.abort();
		}
		this.m_oXDR = null;
    	CollectGarbage();
	}
	 
	this.initialiseLogger = function()
	{
		if (this.m_oSL4B_Logger == null)
		{
			this.m_oSL4B_Logger = window.parent.SL4B_Logger;
			this.m_oSL4B_DebugLevel = window.parent.SL4B_DebugLevel;
			
			this.m_oSL4B_Logger.log(this.m_oSL4B_DebugLevel.const_INFO_INT, "SL4B_StreamingType2.initialiseLogger: using " +
				this.m_sXmlHttpRequestType + " SL4B_StreamingType2 object");
		}
	};
	
	this.getParameter = function(l_sName) 
	{
		var l_oMatch = window.location.search.match(new RegExp("[?&]" + l_sName + "=([^&]*)"));
		return ((l_oMatch == null)?null:l_oMatch[1]);
	};
	
	this.setDomain = function () 
	{
		if (!this.m_bDomainSet)
		{
			var l_sCommonDomain = this.getParameter("domain");
			if (l_sCommonDomain != null)
			{
				this.m_bDomainSet = true;
				document.domain = l_sCommonDomain;
			}
			
			this.m_eParent = window.parent;
		}
	};
	

	this.onProgressWrapper = function() 
	{
		if (window.SL4B_StreamingType7 !== undefined)
		{
			SL4B_StreamingType7.onProgress();
		}
	};

	this.onProgress = function()
	{
		this.initialiseLogger(); 
		
		if (!g_bStopped)
		{
			var l_sMessage = this.m_oXDR.responseText;
			var l_nFirstAPos = l_sMessage.indexOf("a(\"");
			if (l_nFirstAPos != -1)
			{
				if (this.m_nEndPosition == 0 )
				{
					this.m_nEndPosition = l_nFirstAPos;
				}
				var l_sPacket = l_sMessage.substring(this.m_nEndPosition);
				this.m_nEndPosition = l_sMessage.length;
				
				this.m_oSL4B_Logger.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT, "SL4B_StreamingType7.onProgress: data received < " + l_sPacket);
			
				var l_pPackets = l_sPacket.split('\n');
				for (var i in l_pPackets)
				{
					var s = l_pPackets[i];
					if (s.length > 0)
					{
						if (s.substring(0,2) == "a(")
						{
							s = s.substring(3, s.length-3);
							this.m_oSL4B_Logger.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT, "SL4B_StreamingType7.onReadyStateChange: data received << " + s);
							this.m_eParent.a(s);
						}
						else if (s.substring(0,2) == "z(")
						{
							this.m_eParent.z();
						}
					}
				}
			}
		}
	};

	this.onErrorWrapper = function() 
	{
		if (window.SL4B_StreamingType7 !== undefined)
		{
			SL4B_StreamingType7.onError();
		}
	};

	this.onError = function() 
	 {
     }
     
         
	this.loaded = function() 
	{		
		this.setDomain();
		
		try
		{
			window.parent.SL4B_ConnectionProxy.getInstance().setResponseHttpRequest(null, this.getParameter("uniqueid"));
		}
		catch (e)
		{
			alert("failed to set setResponseHttpRequest " + e.toString());
		}		
		
		this.m_oXDR = new XDomainRequest();
		
		this.m_oXDR.onerror = this.onErrorWrapper;
        this.m_oXDR.onprogress = this.onProgressWrapper;
        this.m_oXDR.open("GET", "/RTTP-TYPE5");
		this.m_oXDR.send(); 
		
		window.onunload = this.onunload;
		
	};
};
SL4B_StreamingType7.loaded();
