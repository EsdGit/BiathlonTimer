package com.esd.esd.biathlontimer;

import java.io.ObjectInputValidation;
import java.util.ArrayList;

/**
 * Created by NIL_RIMS_2 on 09.12.2016.
 */

public class ErrorsBuffer<T>
{
    private T[] _buffer;
    private int _tail = 0;
    private int _head = 0;
    private int _capacity;
    private boolean _isEmpty;

    public ErrorsBuffer(int capacity)
    {
        _buffer = (T[])new Object[capacity];
        _capacity = capacity;
    }

    public void Write(T val)
    {
        _buffer[_tail] = val;
        _tail++;
        _head++;
        _isEmpty = false;
        if(_tail >= _buffer.length)
        {
            _tail = 0;
        }
    }

    // Здесь всё неправильно
    public T Read()
    {
        T val;
        _tail--;
        val = _buffer[_tail];
        if(_tail <=0)
        {
            _tail = _capacity;
            _isEmpty = true;
        }
        return val;
    }

    public int GetCapacity()
    {
        return _buffer.length;
    }

    public boolean IsFull()
    {
        if(_buffer.length == _capacity)
        {
            return true;
        }
        else return false;
    }

    public boolean IsEmpty()
    {
        return _isEmpty;
    }
}
