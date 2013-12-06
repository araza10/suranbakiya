<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<title>Tajikistan Summary Page</title>
		<link rel="stylesheet" type="text/css" href="css/reset.css" tppabs="http://www.xooom.pl/work/magicadmin/css/reset.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="css/grid.css" tppabs="http://www.xooom.pl/work/magicadmin/css/grid.css" media="screen" />
        <link rel="stylesheet" type="text/css" href="css/styles.css" tppabs="http://www.xooom.pl/work/magicadmin/css/styles.css" media="screen" />
<!--          <script type="text/css" src="reset.css" />
         <script type="text/css" src="grid.css" />
         <script type="text/css" src="styles.css" /> -->
         <script type="text/javascript">
         function getValue(val){
        	 
        	   var picaso = document.getElementById('pico');
        	   
        	   var dropdowntext = picaso.options[picaso.selectedIndex].text;
        	   window.location = 'StatusServlet?locationType='+dropdowntext;
        	}
         </script>
	</head>
	<body>

        
            <!-- Summary -->
            <div class="grid_5">
                <div class="module">
                        <h2><span>Overall Summary</span></h2>
                        
                        <div class="module-body">
                        
                        	<p>
                                <strong>Date: </strong>${attributedate}<br />
                                <strong>Time: </strong>${attributetime}<br />
                                <strong>System Status: </strong>The system is running correctly
                            </p>
                        
                             <div>
                                 <div class="indicator">
                                     <div style="width: 23%;"></div><!-- change the width value (23%) to dynamically control your indicator -->
                                 </div>
                                 <p><strong>Total Screened: </strong> ${ScreeningTotal}<br/>
                                 <strong>Total Suspects: </strong>  ${SuspectTotal}<br/>
                                 <strong>Total NonSuspects: </strong>  ${NonSuspectTotal}<br/>
                                 <strong>Number of Centers Active: </strong>  20 <br/>
                                 <strong>Wrong Suspect Entries: </strong>  ${WrongEntries}<br/>
                                 <strong>Correct Suspect Entries: </strong>  ${totalCorrectSuspectEntries}<br/>
                                <strong>Working Days: </strong>  ${workingDays}<br/>
                                <strong>Screening/day Overall: </strong>  ${screensperdayperoverall}<br/>
                                <strong>Screening/day per Center: </strong>  ${screensperdaypercenter}<br/>
                                <strong>Percent of suspects among total screened: </strong>  ${percentage}%<br/>
                                 </p>
                             </div>



                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            
            
             <!-- Location Type Summary -->
                        <div class="grid_5">
                <div class="module">
                        <h2><span>Location Type Summary</span></h2>
                        
                        <div class="module-body">
                      
                        
                             <div>
                                 <div class="indicator">
                                     <div style="width: 23%;"></div><!-- change the width value (23%) to dynamically control your indicator -->
                                 </div>
                                   <p><strong>Select Location Type:</strong> 
                    <select class="input-short" id="pico" onchange="getValue(this.value)">
                    	<option value="1">Select filter</option>
                        <option value="2">All PolyClinics</option>
                        <option value="3">All Diabetes Centers</option>
                        <option value="4">Prison Centers</option>
                        
                    </select></p><p>${LocationName}</p>
                                 <p><strong>Total Screened: </strong> ${allpoly}<br/>
                                 <strong>Total Suspects: </strong>  ${allpolySuspects}<br/>
                                 <strong>Total NonSuspects: </strong>  ${allpolyNonSuspects}<br/>
                                </p>
                               
                                
                                 
                             </div>



                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            
            <div style="clear:both;"></div>
            <!-- End .grid_5 -->
            

                
			
                
               <!-- Location Type Summary -->
                        <div class="grid_5">
                <div class="module">
                        <h2><span>Screens by Locations</span></h2>
                        
                        <div class="module-body">
                      
                        
                             <div>
                                 <div class="indicator">
                                     <div style="width: 23%;"></div><!-- change the width value (23%) to dynamically control your indicator -->
                                 </div>
                                 
               
                                 <p>
                                 <strong>CENTRALPOLYCLINIC: </strong> ${CENTRALPOLYCLINIC}<br/>
                                 <strong>POLYDUSHANBE2: </strong>  ${POLYDUSHANBE2}<br/>
                                 <strong>POLYDUSHANBE3: </strong>  ${POLYDUSHANBE3}<br/>
                                 <strong>POLYDUSHANBE4: </strong> ${POLYDUSHANBE4}<br/>
                                 <strong>POLYDUSHANBE5: </strong>  ${POLYDUSHANBE5}<br/>
                                 <strong>POLYDUSHANBE6: </strong>  ${POLYDUSHANBE6}<br/>
                                 <strong>POLYDUSHANBE7: </strong> ${POLYDUSHANBE7}<br/>
                                 <strong>POLYDUSHANBE8: </strong>  ${POLYDUSHANBE8}<br/>
                                 <strong>POLYDUSHANBE9: </strong>  ${POLYDUSHANBE9}<br/>
                                 <strong>POLYDUSHANBE10: </strong> ${POLYDUSHANBE10}<br/>
                                 <strong>POLYDUSHANBE11: </strong>  ${POLYDUSHANBE11}<br/>
                                 <strong>POLYDUSHANBE12: </strong>  ${POLYDUSHANBE12}<br/>
                                 <%-- <strong>POLYDUSHANBE13: </strong> ${allpoly}<br/> --%>
                                 <strong>POLYDUSHANBE14: </strong>  ${POLYDUSHANBE14}<br/>
                                 <strong>POLYTURSUNZADE1: </strong>  ${POLYTURSUNZADE1}<br/>
                                 <strong>POLYTURSUNZADE2: </strong> ${POLYTURSUNZADE2}<br/>
                                 <strong>DIABETESDUSHANBE1: </strong>  ${DIABETESDUSHANBE1}<br/>
                                 <strong>DIABETESDUSHANBE2: </strong>  ${DIABETESDUSHANBE2}<br/>
                                 <strong>PRISONSYSTEM: </strong>  ${PRISONSYSTEM}<br/>
                                 <strong>RUDAKI: </strong>  ${RUDAKI}<br/>
                                 <strong>POLYDUSHANBE1: </strong>  ${POLYDUSHANBE1}<br/>
                                 
                                </p>
                               
                                
                                 
                             </div>



                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            
            <div style="clear:both;"></div>
            <!-- End .grid_5 -->            
           
            
            
 	</body>
</html>