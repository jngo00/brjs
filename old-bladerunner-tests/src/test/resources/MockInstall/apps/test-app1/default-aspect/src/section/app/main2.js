section.app.main2 = function() { }
caplin.extend("section.app.main2","section.app.main1");
section.app.main2.prototype.start = function() {
	write("hello from section.app.main2");
	new section.a.app.bladeset1();
}