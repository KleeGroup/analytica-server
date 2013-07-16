
var clockTimerID = null;
var monNames = new Array ('',
    'Jan','Fev','Mar',
    'Avr','Mai','Jui',
    'Juil','Août','Sep',
    'Oct','Nov','Dec');
var dayNames = new Array ('',
    'Dimanche','Lundi',
    'Mardi','Mercredi',
    'Jeudi','Vendredi',
    'Samedi' );

function startClock () {

  if ( clockTimerID != null ) {
    clearTimeout ( clockTimerID );
  }

  update_clock_display ();
}

function update_clock_display () {
 // $('.spark-chart').sparkline('html', {type: 'bar', barColor: 'red'} );
  

  // GET THE CURRENT SYSTEM DATE/TIME INFORMATION
  var dateNow = new Date ();

  // SPLIT THE DATE INTO VARIOUS DATE/TIME COMPONENTS
  var hour = dateNow.getHours ();
  var mins = dateNow.getMinutes ();
  var secs = dateNow.getSeconds ();
  var date = dateNow.getDate ();
  var daynum = dateNow.getDay () + 1;
  var monnum = dateNow.getMonth () + 1;
  var year = dateNow.getYear ();

  // SECONDARY CALCULATION FOR CLOCK DIGITS, ETC.
  var hour1 = Math.floor ( hour / 10 );
  var hour2 = hour % 10;
  var mins1 = Math.floor ( mins / 10 );
  var mins2 = mins % 10;
  var secs1 = Math.floor ( secs / 10 );
  var secs2 = secs % 10;
  var date1 = Math.floor (date / 10);
  var date2 = date % 10;
  if ( year < 200 ) {
    year += 1900;
  }

  // NOW START THE ROUTINES TO UPDATE THE CLOCK
  updateDate('fullDate',monNames,date1,date2,monnum,year);
  updateTime('bigTime',hour1,hour2,mins1,mins2);
  updateDayName('dayName',dayNames,daynum);
  updateSeconds('seconds',secs1,secs2);

  // THIS LINE RECURSIVELY CALLS IT'S OWN ROUTINE EVERY SECOND
  clockTimerID = setTimeout ("update_clock_display ()" ,1000)
}

var prevDate = "";
function updateDate(objId,monNames,date1,date2,monnum,year) {

  var htmstr = date1 + '' + date2 + '-' + monNames[monnum] + '-' + year;
  if (htmstr != prevDate) {
    squirtHTML(objId,htmstr);
  }
  prevDate = htmstr;
}

var prevTime = "";
function updateTime(objId,hour1,hour2,mins1,mins2) {

  var htmstr = hour1 + '' + hour2 + ':' + mins1 + '' + mins2;
  if (htmstr != prevTime) {
    squirtHTML(objId,htmstr);
  }
  prevTime = htmstr;
}

var prevDayName = "";
function updateDayName(objId,dayNames,dayNum) {

  var htmstr = dayNames[dayNum] + '  '; 
  if (htmstr != prevDayName) {
    squirtHTML(objId,htmstr);
  }
  prevDayName = htmstr;
}

var prevSeconds = "";
function updateSeconds(objId,secs1,secs2) {

  var htmstr = secs1 + '' + secs2;
  if (htmstr != prevSeconds) {
   // squirtHTML(objId,htmstr);
  }
  prevSeconds = htmstr;
}

function convertObj(objStr) {

  var objID = null;

  if (document.getElementById) {
    objID = document.getElementById(objStr);
  } else if (document.all) {
    objID = eval("document." + objStr);
  } else if (document.layers) {
    objID = eval("document.layers." + objStr);
  }

  return(objID);
}

function squirtHTML(objStr,htmstr) {

  var objID = convertObj(objStr);
  objID.innerHTML = htmstr;
}


