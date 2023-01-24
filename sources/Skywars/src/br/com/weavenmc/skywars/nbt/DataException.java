package br.com.weavenmc.skywars.nbt;

public class DataException extends Exception
{
  private static final long serialVersionUID = 5806521052111023788L;

  public DataException(String msg)
  {
    super(msg);
  }

  public DataException()
  {
  }
}