function(doc){
	if (doc.doctype == 'mirror_of'){
		emit(doc.target_url, {'_id': doc._id});
	}
}
