#!/usr/bin/env python
# coding: utf-8

# In[660]:


import pandas as pd
import os
import seaborn as sns
import matplotlib.pyplot as plt
import numpy as np


# In[661]:


df = pd.read_csv("Life_Expectancy.csv")


# In[ ]:





# In[662]:


df.head()


# In[663]:


df.tail()


# In[664]:


df.shape


# In[665]:


df.info()


# In[666]:


#Checking for Missing Values


# In[667]:


df.isnull().sum()


# In[668]:


df.fillna(df.mean(),inplace=True)


# In[669]:


df.isnull().sum()


# In[670]:


df.describe()


# In[671]:


#EDA


# In[672]:


country_vs_life = df.groupby('Country', as_index=False)['Life expectancy '].mean()
country_vs_life.sort_values(by = 'Life expectancy ', ascending=False).head(10)


# In[673]:


country_vs_life.sort_values(by = 'Life expectancy ', ascending=True).head(10)


# In[674]:


df.groupby('Status', as_index=False)['Life expectancy '].mean()


# In[675]:


Developed = df[df['Status']=='Developed']
Developing = df[df['Status']=='Developing']
developed_pl= Developed.groupby(['Year'])['Life expectancy '].max()
developing_pl = Developing.groupby(['Year'])['Life expectancy '].max()
dvp = developed_pl.plot(linewidth=4,figsize=(15,8))
dp = developing_pl.plot(linewidth=2,figsize=(15,8))
dvp.legend(['Developed'],loc=4)


# In[676]:


plt.figure(figsize=(15,10))
cmap = sns.diverging_palette(600, 5, as_cmap=True)
sns.heatmap(df.corr(), cmap =cmap, center=0, annot=False, square=True);


# In[677]:


y= df["Life expectancy "]
df1= df.copy()
df1=  df1.drop(["Life expectancy "], axis=1)

categorical= df1.select_dtypes(include= "O")
numerical= df1.select_dtypes(exclude= "O")


# In[678]:


#Adult Mortality rate vs Life expectancy
#has a significance effect
sns.regplot(x = df['Adult Mortality'], y = y, color = 'red')
plt.tight_layout()    
sns.set(color_codes=True)
plt.show()


# In[679]:


sns.regplot(x = df['Alcohol'], y = y, color = 'blue')
plt.tight_layout()    
sns.set(color_codes=True)
plt.show()


# In[680]:


#BMI vs Life expectancy
#comaparatively positive relationship
sns.regplot(x = df[' BMI '], y = y, color = 'green')
plt.tight_layout()    
sns.set(color_codes=True)
plt.show()
plt.savefig("pic2.jpg")


# In[681]:


#BMI vs Life expectancy
#significantly negative relationship
sns.regplot(x = df[' HIV/AIDS'], y = y, color = 'purple')
plt.tight_layout()    
sns.set(color_codes=True)
plt.show()


# In[682]:


#Regression Models


# In[683]:


df.isnull().sum()


# In[684]:


d = pd.concat([df,pd.get_dummies(df["Status"])],axis=1)
d = pd.concat([df,pd.get_dummies(df["Year"])],axis=1)
print(d)


# In[685]:


X=df.drop(["Life expectancy ","Country","Status","Year"], axis=1)
y=df["Life expectancy "]


# In[686]:


from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test= train_test_split(X, y, test_size= 0.30, random_state=9)


# In[687]:


from sklearn.preprocessing import StandardScaler
sc = StandardScaler()
X_train = sc.fit_transform(X_train)
X_test = sc.transform(X_test)


# In[688]:


from sklearn.ensemble import RandomForestRegressor, AdaBoostRegressor, GradientBoostingRegressor
from sklearn.tree import DecisionTreeRegressor
from sklearn.linear_model import LinearRegression

from sklearn.metrics import mean_absolute_error as mape
from sklearn.metrics import mean_squared_error as mse
from sklearn.metrics import r2_score as r2

alg = [RandomForestRegressor(), AdaBoostRegressor(), GradientBoostingRegressor(), DecisionTreeRegressor(), LinearRegression()]
arr =[]


# In[689]:


for i in alg:
    model= i
    model.fit(X_train, y_train)
    y_pred= model.predict(X_test)
    arr.append(y_pred)
    print(i)
    print("Test Root Mean Squared error:", np.sqrt(mse(y_test, y_pred)))
    print("R Square Score:", r2(y_test, y_pred))
    print("Mean Absolute Error:", mape(y_test, y_pred))
    print("-"*70)


# In[690]:


arr


# In[691]:


y = np.array(np.arange(1,883))
x = np.array(arr[4])
x1 = np.array(y_test)
fig=plt.figure(figsize=(15,10))
plt.plot(x,y,"ob", color='b')
plt.plot(x1,y,"ob", color='r')
fig.suptitle('Linear Regression', fontsize=20)
plt.ylabel('Count', fontsize=18)
plt.xlabel('Life Expectancy', fontsize=16)
fig.savefig('linear.jpg')
'''
plt.title("Linear Regression") 
plt.xlabel("Regression Models") 
plt.ylabel("Life Expectancy") 
plt.figure(figsize=(15,10))
figure=plt.plot(x,y,"ob", color='b')
figure=plt.plot(x,y1,"ob", color='r')
#figure.xlabel("Regression Models")
#figure.ylabel("Life Expectancy") 
#figure.legend()
 '''


# In[692]:


#Hypothesis Testing


# In[693]:


#null hypothesis = " hypertension and stroke are dependent"
#alternate hypothesis = 'hyper tension and stroke are not dependent.'
dev=[]
dev.append(len(data[(t==0) & (data['Life expectancy ']<)]))
htp_strn = len(data[(data['hypertension']==1) & (data['stroke']==0)])
htn_strp = len(data[(data['hypertension']==0) & (data['stroke']==1)])
htpn_strn = len(data[(data['hypertension']==0) & (data['stroke']==0)])
a = [htp_strp,htp_strn]
b = [htn_strp, htpn_strn]
c =[a,b]
stat, p,dof,expected = chi2_contingency(c)
if(p<0.001):
    print('null hypothesis is rejected')
else:
    print('null hypothesis is accepted')


# In[ ]:




