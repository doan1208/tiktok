package com.dle.Repository;

import com.dle.bean.database.Document;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DocumentRepository implements PanacheRepository<Document> {
}
