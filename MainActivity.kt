package com.bazlo.app
import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : Activity() {
 private val auth by lazy { FirebaseAuth.getInstance() }
 private val db by lazy { FirebaseFirestore.getInstance() }
 private val cart = mutableListOf<Map<String, Any>>()
 private lateinit var list: LinearLayout
 private lateinit var cartText: TextView
 override fun onCreate(b: Bundle?) { super.onCreate(b); showLogin() }
 private fun showLogin() {
  val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(24,24,24,24)}
  root.addView(TextView(this).apply{text="BAZLO\nHar Cheez, Ek Jagah.";textSize=28f})
  val email=EditText(this).apply{hint="Email"}; val pass=EditText(this).apply{hint="Password";inputType=0x81}; root.addView(email);root.addView(pass)
  val login=Button(this).apply{text="Login"}; val signup=Button(this).apply{text="Create Account"}; root.addView(login);root.addView(signup);setContentView(root)
  login.setOnClickListener{auth.signInWithEmailAndPassword(email.text.toString().trim(),pass.text.toString()).addOnSuccessListener{showShop()}.addOnFailureListener{toast(it.message?:"Login failed")}}
  signup.setOnClickListener{auth.createUserWithEmailAndPassword(email.text.toString().trim(),pass.text.toString()).addOnSuccessListener{showShop()}.addOnFailureListener{toast(it.message?:"Signup failed")}}
 }
 private fun showShop(){
  val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(16,16,16,16)}
  val top=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}; top.addView(TextView(this).apply{text="BAZLO";textSize=25f},LinearLayout.LayoutParams(0,-2,1f)); cartText=TextView(this).apply{text="Cart: 0";textSize=18f};top.addView(cartText);root.addView(top)
  val search=EditText(this).apply{hint="Search products..."};root.addView(search)
  list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};root.addView(ScrollView(this).apply{addView(list)},ViewGroup.LayoutParams(-1,0,1f))
  root.addView(Button(this).apply{text="Place COD Order";setOnClickListener{placeOrder()}});setContentView(root);loadProducts("")
  search.addTextChangedListener(object:android.text.TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){loadProducts(s?.toString()?.trim()?: "")};override fun afterTextChanged(e:android.text.Editable?) {}})
 }
 private fun loadProducts(term:String){db.collection("products").orderBy("name",Query.Direction.ASCENDING).limit(100).get().addOnSuccessListener{snap->list.removeAllViews();snap.documents.forEach{d->val name=d.getString("name")?:"";if(term.isNotBlank()&&!name.contains(term,true))return@forEach;val price=d.getLong("price")?:0;val stock=(d.getLong("stock")?:0).toInt();val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;setPadding(8,18,8,18)};row.addView(TextView(this).apply{text="$name\nRs. $price\nStock: $stock";textSize=17f},LinearLayout.LayoutParams(0,-2,1f));row.addView(Button(this).apply{text="Add";isEnabled=stock>0;setOnClickListener{cart.add(mapOf("productId" to d.id,"name" to name,"price" to price,"qty" to 1L));cartText.text="Cart: ${cart.size}"}});list.addView(row)}}.addOnFailureListener{toast(it.message?:"Could not load products")}}
 private fun placeOrder(){if(cart.isEmpty()){toast("Cart is empty");return};val total=cart.sumOf{(it["price"] as Long)*(it["qty"] as Long)};val uid=auth.currentUser?.uid?:return;val order=hashMapOf("customerUid" to uid,"customerName" to(auth.currentUser?.email?:"Customer"),"total" to total,"status" to "pending","createdAt" to System.currentTimeMillis(),"items" to cart);db.collection("orders").add(order).addOnSuccessListener{cart.clear();cartText.text="Cart: 0";toast("Order placed: ${it.id}")}.addOnFailureListener{toast(it.message?:"Order failed")}}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
}
