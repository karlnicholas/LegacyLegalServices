/* Formatting function for row details - modify as you need */
function getParameterByName(name) {
    url = window.location.href;
    if (url == '') return;
    // This is just to avoid case sensitiveness
    url = url.toLowerCase(); 
    // This is just to avoid case sensitiveness for query parameter name  
    name = name.replace(/[\[\]]/g, '\\$&').toLowerCase();
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}
function format ( table_id ) {
    return '<table class="table table-striped" id="opiniondt_'+table_id+'">'+
    '<thead><tr><th>References</th><th>Section</th><th>Statute Titles</th></tr></thead>'+
    '<tr>'+
      '<td>Full name:</td>'+
      '<td>Extension number:</td>'+
      '<td>Extra info:</td>'+
    '</tr>'+
    '<tr>'+
      '<td>Full name:</td>'+
      '<td>Extension number:</td>'+
      '<td>Extra info:</td>'+
    '</tr>'+
    '</table>';
  }
var iTableCounter=1;
var oInnerTable;

$(document).ready(function() {
	TableHtml = $('#opiniondt').html();
  
    var table = $('#opiniondt').DataTable( {
      paging:    false,
      searching: false, 
      info:      false, 
      rowId: 'id', 
      ajax: {
          url: '../rest/opinion?startDate='+getParameterByName('startDate'),
          dataSrc: "opinions"
        }, 
      columns:[ 
        {   className:      'details-control',
            orderable:      false,
            data:           null,
            defaultContent: '' },
        { data:"opinionDate"},
        { data:"title"}, 
        { data:"name"}
      ],
      order: [[1, 'asc']]
    } );
    /* Add event listener for opening and closing details
     * Note that the indicator for showing which row is open is not controlled by DataTables,
     * rather it is done here
     */
     $('#opiniondt tbody').on('click', 'td.details-control', function () {
         var tr = $(this).closest('tr');
         var row = table.row( tr );   
         
         if ( row.child.isShown() ) {
             //  This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
         }
         else {
            // Open this row
            row.child( format(iTableCounter) ).show();
            tr.addClass('shown');
            // try datatable stuff
            oInnerTable = $('#opiniondt_' + iTableCounter).dataTable({
                ajax: {
                  url: '../rest/section?startDate='+getParameterByName('startDate')+'&id='+tr.attr('id'),
                  dataSrc: "sections"
                },  
                autoWidth: true, 
                deferRender: true, 
                info: false, 
                lengthChange: false, 
                ordering: false, 
                paging: false, 
                scrollX: false, 
                scrollY: false, 
                searching: false, 
                columns:[ 
                   { data:'refCount' },
                   { data:'section.codeRange.sNumber.sectionNumber' }, 
                   { data:'section.title' }
                 ]
            });
            iTableCounter = iTableCounter + 1;
        }
     });
} );
