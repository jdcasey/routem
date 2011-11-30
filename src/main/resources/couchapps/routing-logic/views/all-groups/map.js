function(doc){
	if( doc.doctype == 'group' ){
		emit(doc.group_id,{'_id': doc._id});
	}
}
