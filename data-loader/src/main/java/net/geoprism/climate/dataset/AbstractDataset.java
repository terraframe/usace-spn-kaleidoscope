package net.geoprism.climate.dataset;

abstract public class AbstractDataset
{
  public AbstractDataset()
  {
  }

  public void synchronizeDatabase() throws Throwable
  {
    this.synchronize();

  }

  abstract public void synchronize() throws Throwable;
}
