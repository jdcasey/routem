function(doc){
	if ( doc.doctype == 'route' && doc.group_ids ){
		for( var i=0; i<doc.group_ids.length; i++){
			emit(doc.group_ids[i], {'_id': doc._id});
		}
	}
}
