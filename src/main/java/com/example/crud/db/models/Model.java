package com.example.crud.db.models;

import java.io.Serializable;
import java.util.Date;

public interface Model<T extends Serializable> {
    Date getCreatedDate();
    Date getModifiedDate();
}
