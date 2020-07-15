package com.foxconn.iot.assets.mongo.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.foxconn.iot.assets.mongo.dao.AssetsDao;
import com.foxconn.iot.assets.mongo.document.Asset;

@Component
public class AssetsDaoImpl implements AssetsDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(List<Asset> assets) {
		mongoTemplate.insertAll(assets);
	}

	@Override
	public Page<Asset> query(Long companyId, Pageable pageable) {
		Query query = new Query();
		query.addCriteria(Criteria.where("companyId").is(companyId));
		query.with(pageable);
		List<Asset> assets = mongoTemplate.find(query, Asset.class);		
		return new PageImpl<>(assets, pageable, count(companyId));
	}

	@Override
	public long count(Long companyId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("companyId").is(companyId));
		return mongoTemplate.count(query, Asset.class);
	}

	@Override
	public List<Asset> query(Long companyId, List<Long> ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("companyId").is(companyId));
		query.addCriteria(Criteria.where("id").in(ids));		
		return mongoTemplate.find(query, Asset.class);
	}
}