package com.ibm.common.geojson;

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxLineStrings;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

public final class MultiLineString 
  extends CoordinateGeometry<MultiLineString,LineString,Iterable<LineString>> {

  public static final class Builder 
    extends CoordinateGeometry.Builder<LineString,Iterable<LineString>, MultiLineString, Builder> {

    private final ImmutableList.Builder<LineString> strings = 
      ImmutableList.builder();
    
    public Builder() {
      type(Type.MULTILINESTRING);
    }
    
    public Builder add(LineString line, LineString... lines) {
      this.strings.add(line);
      if (lines != null)
        for (LineString l : lines)
          add(l);
      return this;
    }
    
    public Builder add(Supplier<LineString> line) {
      return add(line.get());
    }
    
    public Builder add(Iterable<LineString> lines) {
      this.strings.addAll(lines);
      return this;
    }
    
    public MultiLineString doGet() {
      return new MultiLineString(this);
    }

    @Override
    protected Iterable<LineString> coordinates() {
      return strings.build();
    }
    
  }
    
  protected MultiLineString(
    Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<LineString> iterator() {
    return coordinates().iterator();
  }

  @Override
  protected MultiLineString makeWithBoundingBox() {
    return new MultiLineString.Builder()
      .from(this)
      .add(this)
      .boundingBox(
        calculateBoundingBoxLineStrings(this)).get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<MultiLineString,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(MultiLineString obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<LineString> list = (Iterable<LineString>) val;
        builder.strings.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected MultiLineString.Builder builder() {
      return GeoMakers.multiLineString();
    }
  }
}