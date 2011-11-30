function(doc){
	if( doc.doctype == 'mirror_of' ){
		emit([doc.canonical_url, doc.target_url],{'_id': doc._id});
	}
}
