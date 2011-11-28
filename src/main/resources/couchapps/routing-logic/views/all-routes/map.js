function(doc){
	if( doc.doctype == 'route' ){
		emit(doc._id,{'_id': doc._id});
	}
}
