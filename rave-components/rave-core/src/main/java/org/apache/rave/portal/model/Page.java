/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.rave.portal.model;

import org.apache.rave.persistence.BasicEntity;
import org.codehaus.jackson.annotate.JsonManagedReference;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

/**
 * A page, which consists of regions, and which may be owned by a {@link User} (note the ownership will likely need to
 * become more flexible to enable things like group ownership in the future).
 * 
 * TODO RAVE-231: not all database providers will be able to support deferrable constraints
 * so for the time being I'm commenting out the owner/render sequence since it
 * will get updated in batches and blow up
 * @UniqueConstraint(columnNames={"owner_id","render_sequence"}
 * 
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Table(name="page", uniqueConstraints={@UniqueConstraint(columnNames={"owner_id","name"})})
@NamedQueries({
        @NamedQuery(name = "Page.getByUserId", query="SELECT p FROM Page p WHERE p.owner.entityId = :userId ORDER BY p.renderSequence")
})
@Access(AccessType.FIELD)
public class Page implements BasicEntity, Serializable {
    private static final long serialVersionUID = 1L;
     
    @XmlAttribute(name="id")
    @Id @Column(name="entity_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pageIdGenerator")
    @TableGenerator(name = "pageIdGenerator", table = "RAVE_PORTAL_SEQUENCES", pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT", pkColumnValue = "page", allocationSize = 1, initialValue = 1)
    private Long entityId;

    @XmlElement
    @Basic(optional=false) @Column(name="name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Basic(optional=false) @Column(name="render_sequence")
    private Long renderSequence;

    @ManyToOne
    @JoinColumn(name="page_layout_id")
    private PageLayout pageLayout;

    @XmlElement(name="region")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("renderOrder")
    @JoinColumn(name="page_id")
    private List<Region> regions;

    public Page() {
    }
    
    public Page(Long entityId) {
        this.entityId = entityId;
    }

    public Page(Long entityId, User owner) {
        this.entityId = entityId;
        this.owner = owner;
    }

    /**
     * Gets the persistence unique identifier
     *
     * @return id The ID of persisted object; null if not persisted
     */
    @Override
    public Long getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the user defined name of the page
     *
     * @return Valid name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the {@link User} that owns the page
     *
     * @return Valid {@link User}
     */
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gets the order of the page instance relative to all pages for the owner (useful when presenting pages in an
     * ordered layout like tabs or an accordion container)
     *
     * @return Valid, unique render sequence
     */
    public Long getRenderSequence() {
        return renderSequence;
    }

    public void setRenderSequence(Long renderSequence) {
        this.renderSequence = renderSequence;
    }

    /**
     * Gets the {@link PageLayout}
     *
     * @return Valid {@link PageLayout}
     */
    public PageLayout getPageLayout() {
        return pageLayout;
    }

    public void setPageLayout(PageLayout pageLayout) {
        this.pageLayout = pageLayout;
    }

    /**
     * Gets the widget containing {@link Region}s of the page
     *
     * @return Valid list of {@link Region}s
     */
    @JsonManagedReference
    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Page other = (Page) obj;
        if (this.entityId != other.entityId && (this.entityId == null || !this.entityId.equals(other.entityId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.entityId != null ? this.entityId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Page{" + "entityId=" + entityId + ", name=" + name + ", owner=" + owner + ", renderSequence=" + renderSequence + ", pageLayout=" + pageLayout + ", regions=" + regions + '}';
    }    
}