/**
 * <code>PresentationModel</code> is an abstract base class that all presentation models
 * must extend, and so is not constructed directly.
 * 
 * @class
 * <p>The <code>PresentationModel</code> is used to transform a set of domain models into
 * a form that is tightly aligned with the view that will be used to display and edit those
 * domain models. As a consequence, the view becomes free of all logic, and components can
 * be tested through the presentation model rather than using brittle, click-based testing
 * via the view.</p>
 * 
 * <p>Presentation models are built as a tree of {@link br.presenter.node.PresentationNode}
 * instances, where the root node must extend <code>PresentationModel</code>, which in turn
 * extends {@link br.presenter.node.PresentationNode}.</p>
 * 
 * @constructor
 * @extends br.presenter.node.PresentationNode
 */
br.presenter.PresentationModel = function()
{
};
br.extend(br.presenter.PresentationModel, br.presenter.node.PresentationNode);

/**
 * Presentation models can use the {@link br.presenter.PresentationModel#getComponentFrame}
 * to receive a reference to the frame containing the {@link br.presenter.component.PresenterComponent}
 * that this model resides within.
 * 
 * @param {br.component.Frame} oComponentFrame The frame within which the
 * presenter component resides.
 */
br.presenter.PresentationModel.prototype.setComponentFrame = function(oComponentFrame)
{
	this.m_oComponentFrame = oComponentFrame;
};

/**
 * Presentation models can use this method to receive a reference to the frame
 * containing the {@link br.presenter.component.PresenterComponent} that this model
 * resides within.
 * 
 * @type br.component.Frame
 */
br.presenter.PresentationModel.prototype.getComponentFrame = function()
{
	return this.m_oComponentFrame;
};

/**
 * Returns the presentation model class name.
 * 
 * @return Presentation model class name.
 * @type String
 */
br.presenter.PresentationModel.prototype.getClassName = function()
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_ABSTRACT_METHOD,
			"br.presenter.PresentationModel.getClassName() has not been implemented.");
};

/**
 * @private
 * @param {String} sPath
 */
br.presenter.PresentationModel.prototype._$setPath = function(oPresenterComponent)
{
	this.m_sPath = "";
	
	for(var sChildToBeSet in this)
	{
		var oChildToBeSet = this[sChildToBeSet];
		
		if(this._isPresenterChild(sChildToBeSet,oChildToBeSet))
		{
			var sCurrentPath = oChildToBeSet.getPath();
			
			if(sCurrentPath === undefined)
			{
				oChildToBeSet._$setPath(sChildToBeSet, oPresenterComponent);
			}
			else
			{
				throw new br.Errors.CustomError(br.Errors.LEGACY, "'" + sCurrentPath + "' and '" + sChildToBeSet + "' are both references to the same instance in PresentationModel.");
			}
		}
	}
	
	this.__oPresenterComponent = oPresenterComponent;
};
