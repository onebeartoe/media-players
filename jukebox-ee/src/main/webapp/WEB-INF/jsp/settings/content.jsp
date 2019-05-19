hi
 d
 
 
 
  
 here are the SETTINGS!
 
 
 
 
 
 
 
 
 
 
    <div class="copyspace">
        <h3>Game Controls</h3>
        <div class="featuredProject">

            
<!-- sytling for the contentBox is from: http://stackoverflow.com/questions/9966890/best-way-to-do-columns-in-html-css -->            
        <div id="contentBox" class="contentBox">

         <!-- columns divs, float left, no margin so there is no space between column, width=1/3 -->
            <div id="column1" class="oneThirdColumn">
                <input type="button" onclick="incrementHomeScore()" value="+">
            </div>

            <div id="column2" class="oneThirdColumn">
                <input type="button" onclick="startClock()" value="Start">
            </div>

            <div id="column3" class="oneThirdColumn">
                <input type="button" onclick="incrementAwayScore()" value="+">
            </div>
        </div>

        <br class="clearingBreak">

        <div id="contentBox" class="contentBox">

         <!-- columns divs, float left, no margin so there is no space between column, width=1/3 -->
            <div id="homeScore" class="oneThirdColumn">
             Home
            </div>

            <div id="column2" class="oneThirdColumn">
             --:--
            </div>

            <div id="awayScore" class="oneThirdColumn">
                Away
            </div>
        </div>

        <br class="clearingBreak">

        <div id="contentBox" class="contentBox">

         <!-- columns divs, float left, no margin so there is no space between column, width=1/3 -->
            <div id="column1" class="oneThirdColumn">

                <input type="button" onclick="decrementHomeScore()" value="-">
            </div>

            <div id="column2" class="oneThirdColumn">
                <input type="button" onclick="stopClock()" value="Stop">
            </div>

            <div id="column3" class="oneThirdColumn">
                <input type="button" onclick="decrementAwayScore()" value="-">
            </div>
        </div>

        <br class="clearingBreak">
            
        </div>
        <br class="clearingBreak">
    </div>

    <div class="copyspace">
        <h3>logs</h3>

        <div class="featuredProject">
            <div id="logs" class="logs">

            </div>
        </div>
    </div>
