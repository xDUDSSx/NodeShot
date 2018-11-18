#target photoshop

var doc = app.activeDocument;
var sel = doc.selection;//main document selection
var rows = 9;
var cols = 3;
var cell  = 64;//size of a cell/display image/region of interest
var space= 0; //space in between cells
var dname = doc.name.substr(0,doc.name.length-4);
var dir = doc.path.toString()+"/";
var dpi72 = 182.88036576073152146304292608585;
var options = new ExportOptionsSaveForWeb();
options.format = SaveDocumentType.PNG;
//options.quality = 75;

//*
for(var y = 0; y < cols; y++){
        for(var x = 0 ; x < rows; x++){
            var xpos = (x * (cell+space))+((1-y%2) * space);//+ di-grid offset
             var ypos = (y * (cell+space));

             try{

             sel.select([[xpos,ypos],[xpos+cell,ypos],[xpos+cell,ypos+cell],[xpos,ypos+cell]],SelectionType.REPLACE,0,false);
             sel.copy(true);  

             var nname = dname + "_"+((x+1)) + "_" + ((y+1));
             //$.writeln("["+x+"]["+y+"]" +nname +"\n"+sel.bounds);
                //*
             var small = app.documents.add(cell,cell,dpi72,dname,NewDocumentMode.RGB,DocumentFill.TRANSPARENT);

             app.activeDocument = small;

             small.paste();
             //small.exportDocument (new File(dir + "/" + nname + ".png"), ExportType.SAVEFORWEB, options);
	     var saveFile = new File(dir + "/" + nname + ".png");  
             var pngOpts = new ExportOptionsSaveForWeb;   
             pngOpts.format = SaveDocumentType.PNG  
             pngOpts.PNG8 = true;   
             pngOpts.transparency = false;   
             pngOpts.interlaced = false;   
             pngOpts.quality = 100;  
             activeDocument.exportDocument(saveFile,ExportType.SAVEFORWEB,pngOpts);               

	     small.close(SaveOptions.DONOTSAVECHANGES);
		
             app.activeDocument = doc;
             //*/
             }catch(err){
                 x = rows;
                 y = cols;
                 alert(err);
             }
    }
}