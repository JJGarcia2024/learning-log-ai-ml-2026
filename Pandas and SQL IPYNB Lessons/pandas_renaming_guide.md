# Pandas Renaming Guide: Columns, Rows, and Axes

This guide summarizes how to change the names of different structural parts of your DataFrame using the `.rename()` and `.rename_axis()` methods.

---

## 1. Renaming Columns
You will use this all the time to clean up messy headers or standardize your data. You pass a dictionary to the `columns` parameter mapping the `{'old_name': 'new_name'}`.

**The Code:**
```python
reviews.rename(columns={'points': 'score'})
```

**What it does (Sample Output):**
Notice how the column formerly known as `points` is now `score`.
```text
           country   description           designation     score   price  ...
0          Italy     Aromas include...     Vulkà Bianco       87     NaN  ...
1          Portugal  This is ripe...       Avidagos           87    15.0  ...
2          US        Tart and snappy...    NaN                87    14.0  ...
```

---

## 2. Renaming Specific Row Indices
Just like renaming columns, you can rename specific row labels by passing a dictionary to the `index` parameter. Note: It is generally rare to do this manually in the real world (usually you would use `.set_index()` to make a whole column the index instead).

**The Code:**
```python
reviews.rename(index={0: 'firstEntry', 1: 'secondEntry'})
```

**What it does (Sample Output):**
Notice how the row numbers `0` and `1` on the far left have been replaced with strings.
```text
               country   description           designation    points   price  ...
firstEntry     Italy     Aromas include...     Vulkà Bianco       87     NaN  ...
secondEntry    Portugal  This is ripe...       Avidagos           87    15.0  ...
2              US        Tart and snappy...    NaN                87    14.0  ...
```

---

## 3. Renaming the Axes (Index Names)
Every row index and column index in pandas can have its own name (an "axis name"). This is completely separate from the labels themselves. You use `.rename_axis()` to label the structure.

**The Code:**
```python
reviews.rename_axis("wines", axis='rows').rename_axis("fields", axis='columns')
```

**What it does (Sample Output):**
Look at the top-left corner. The column headers are now grouped under the name `fields`, and the row numbers are grouped under the name `wines`.

```text
fields     country   description           designation    points   price  ...
wines                                                                      
0          Italy     Aromas include...     Vulkà Bianco       87     NaN  ...
1          Portugal  This is ripe...       Avidagos           87    15.0  ...
2          US        Tart and snappy...    NaN                87    14.0  ...
```
