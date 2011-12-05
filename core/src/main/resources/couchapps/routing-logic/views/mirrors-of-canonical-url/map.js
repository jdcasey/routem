function(doc){
	if (doc.doctype == 'mirror_of'){
		emit(doc.canonical_url, {'_id': doc._id});
	}
}
