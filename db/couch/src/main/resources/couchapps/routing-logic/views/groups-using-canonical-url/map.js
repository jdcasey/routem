function(doc){
	if (doc.doctype == 'group'){
		emit(doc.canonical_url, {'_id': doc._id});
	}
}
