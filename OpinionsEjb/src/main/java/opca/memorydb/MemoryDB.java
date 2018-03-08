package opca.memorydb;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlRootElement;

import opca.model.OpinionBase;
import opca.model.StatuteCitation;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement
public class MemoryDB {
    private TreeSet<OpinionBase> opinionTable = new TreeSet<OpinionBase>();
    private TreeSet<StatuteCitation> statuteTable = new TreeSet<StatuteCitation>();
    
    public TreeSet<OpinionBase> getOpinionTable() {
        return opinionTable;
    }
    @XmlElement
    public void setOpinionTable(TreeSet<OpinionBase> opinionTable) {
        this.opinionTable = opinionTable;
    }
    public TreeSet<StatuteCitation> getStatuteTable() {
        return statuteTable;
    }
    @XmlElement
    public void setStatuteTable(TreeSet<StatuteCitation> statuteTable) {
        this.statuteTable = statuteTable;
    }

}
